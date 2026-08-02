const Reservation = require("../models/Reservation");

const {
    calculatePurposeScores
} = require("../services/geminiService");

function convertToMinutes(time){

    if (!time) return 0;

    const [hour, minute] = time.split(":").map(Number);

    return hour * 60 + minute;

}

function convertToTime(minutes){
    const hour = Math.floor(minutes / 60);
    const minute = minutes % 60;
    return `${String(hour).padStart(2,"0")}:${String(minute).padStart(2,"0")}`;
}
async function findAlternateTime(
    resource,
    reservation,
    temporaryAllocations = []
){
    const start = convertToMinutes(resource.workingStartTime);
    const end = convertToMinutes(resource.workingEndTime);
    const duration = reservation.durationHours * 60;
    const requested = convertToMinutes(reservation.startTime);
    const existingReservations =
        await Reservation.find({
            requestedResource: resource._id,
            date: reservation.date,
            status:{
                $in:[
                    "APPROVED",
                    "ALTERNATIVE_APPROVED"
                ]
            }

        });
    const occupied=[];
    existingReservations.forEach(r=>{

    const actualStart =
        r.alternativeStartTime ||
        r.startTime;

    occupied.push({

        start:
            convertToMinutes(actualStart),

        end:
            convertToMinutes(actualStart)
            +
            r.durationHours * 60

    });

});
    temporaryAllocations.forEach(slot=>{

    if(
        slot.resource.toString()===
        resource._id.toString()
    ){

        occupied.push({

            start:
                slot.start,

            end:
                slot.end

        });

    }

});
    const isFree=(candidate)=>{
        for(const slot of occupied){
            if(candidate < slot.end && candidate + duration > slot.start){
                return false;
            }
        }
        return true;
    };
    for(
        let current=requested+30;
        current+duration<=end;
        current+=30
    ){
        if(isFree(current)){
            return convertToTime(current);
        }
    }
    for(
        let current=requested-30;
        current>=start;
        current-=30
    ){
        if(isFree(current)){
            return convertToTime(current);
        }
    }
    return null;
}
const calculateCapacityScore = (
    resourceCapacity,
    participantCount
) => {
    if (participantCount > resourceCapacity) {
        return 0;
    }
    if (resourceCapacity <= 0)
    return 0;

return (
    participantCount /
    resourceCapacity
) * 100;
};
const calculateFairUsageScore = async (
    userId
) => {
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(
        thirtyDaysAgo.getDate() - 30
    );
    const recentAllocations =
        await Reservation.countDocuments({
            user: userId,
            status: {
                $in: [
                    "APPROVED",
                    "ALTERNATIVE_APPROVED"
                ]
            },
            allocatedAt: {
    $gte: thirtyDaysAgo
}
        });
    return Math.max(
        0,
        100 - recentAllocations * 5
    );
};
const calculateFinalScore = async (
    reservation,
    resource
) => {
    const capacityScore =
        calculateCapacityScore(
            resource.capacity,
            reservation.participantCount
        );
    const fairUsageScore =
        await calculateFairUsageScore(
            reservation.user
        );
    const purposeScore =
    reservation.purposeScore;
    return (
        capacityScore * 0.5 +
        fairUsageScore * 0.3 +
        purposeScore * 0.2
    );
};
const allocateCapacityResources = async (
    reservations,
    resources
) => {
    const scoredReservations = [];
    for (const reservation of reservations) {
        const requestedResource =
            resources.find(
                resource =>
                    resource._id.toString() ===
                    reservation.requestedResource._id.toString()
            );
        if (
            !requestedResource ||
            requestedResource.capacity <
            reservation.participantCount
        ) {
            continue;
        }
        const score =
            await calculateFinalScore(
                reservation,
                requestedResource
            );
        scoredReservations.push({
            reservation,
            score
        });
    }

    scoredReservations.sort(
        (a, b) => b.score - a.score
    );

    const allocations = [];

    const failures = [];

    const allocatedResources =
        new Set();

    for (const item of scoredReservations) {

        const requestedResourceId =
            item.reservation
                .requestedResource
                ._id
                .toString();

        if (
            allocatedResources.has(
                requestedResourceId
            )
        ) {

            failures.push({

                reservation:
                    item.reservation,

                score:
                    item.score

            });

            continue;

        }

        const resource =
            resources.find(
                resource =>
                    resource._id.toString() ===
                    requestedResourceId
            );

        if (
            !resource ||
            resource.capacity <
            item.reservation
                .participantCount
        ) {

            failures.push({

                reservation:
                    item.reservation,

                score:
                    item.score

            });

            continue;

        }

        allocations.push({

            reservation:
                item.reservation._id,

            resource:
                resource._id,

            score:
                item.score

        });

        allocatedResources.add(
            requestedResourceId
        );

    }

    return {
        allocations,
        failures
    };

};
const executeAllocations = async (
    allocations
) => {

    if (!allocations.length) return;

    const bulkOps =
        allocations.map(
            allocation => ({
                updateOne: {
                    filter: {
                        _id:
                            allocation.reservation
                    },
                    update: {
                        $set: {

    allocatedResource:
        allocation.resource,

    score:
        allocation.score,

    allocationType:
        "REQUESTED",

    status:
        "APPROVED",

    allocatedAt:
        new Date()

}
                    }
                }
            })
        );

    await Reservation.bulkWrite(
        bulkOps
    );

};

const calculateQuantityScore = (
    availableUnits,
    quantityRequired
) => {

    if (
        quantityRequired > availableUnits
    ) {
        return 0;
    }
    if (availableUnits <= 0)
    return 0;

    return (
        quantityRequired /
        availableUnits
    ) * 100;

};

const calculateQuantityFinalScore =
async (
    reservation,
    resource
) => {

    const quantityScore =
        calculateQuantityScore(
            resource.availableUnits,
            reservation.quantityRequired
        );

    const fairUsageScore =
        await calculateFairUsageScore(
            reservation.user
        );

   const purposeScore =
    reservation.purposeScore;

    return (

        quantityScore * 0.2 +

        fairUsageScore * 0.5 +

        purposeScore * 0.3

    );

};

const allocateQuantityResources = async (
    reservations,
    resource
) => {

    const scoredReservations = [];

    for (const reservation of reservations) {

        const score =
            await calculateQuantityFinalScore(
                reservation,
                resource
            );

        scoredReservations.push({
            reservation,
            score
        });

    }

    scoredReservations.sort(
        (a, b) => {

            if (b.score !== a.score) {
                return b.score - a.score;
            }

            return (
                new Date(a.reservation.createdAt) -
                new Date(b.reservation.createdAt)
            );

        }
    );

    const allocations = [];

    const failures = [];

    const allocatedReservations = [];

    for (const item of scoredReservations) {

        const reservation =
            item.reservation;

        const requiredUnits =
            reservation.quantityRequired;

        const currentStart =
            convertToMinutes(
                reservation.startTime
            );

        const currentEnd =
            currentStart +
            reservation.durationHours * 60;

        let usedUnits = 0;

        for (const allocated of allocatedReservations) {

            const overlap =

                currentStart < allocated.end &&
                currentEnd > allocated.start;

            if (overlap) {

                usedUnits +=
                    allocated.units;

            }

        }

        const availableUnits =
            resource.availableUnits -
            usedUnits;

        if (
            availableUnits >=
            requiredUnits
        ) {

            allocations.push({

                reservation:
                    reservation._id,

                resource:
                    resource._id,

                score:
                    item.score

            });

            allocatedReservations.push({

                start:
                    currentStart,

                end:
                    currentEnd,

                units:
                    requiredUnits

            });

        }

        else {

            failures.push({

                reservation,

                resource:
                    resource._id,

                score:
                    item.score

            });

        }

    }

    return {

        allocations,

        failures

    };

};

const splitFailedReservations = (
    failures
) => {

    const specificResource = [];

    const alternateResource = [];

    const alternateTime = [];


    for (const item of failures) {

        const preference =
            item.reservation
            .allocationPreference;

        if (
            preference ===
            "SPECIFIC_RESOURCE"
        ) {

            specificResource.push(
                item
            );

        }

        else if (
            preference ===
            "ALTERNATE_RESOURCE"
        ) {

            alternateResource.push(
                item
            );

        }


        else {

           alternateTime.push(
                item
            );

        }

    }

    return {

        specificResource,

        alternateResource,

        alternateTime

    };

};


const executeWaitlist = async (
    waitlist
) => {

    if (!waitlist.length) return;

    const bulkOps =
        waitlist.map(
            item => ({
                updateOne: {

                    filter: {
                        _id:
                            item.reservation._id
                    },

                    update: {
                        $set: {

                            score:
                                item.score,

                            status:
                                "WAITLISTED"
                        }
                    }

                }
            })
        );

    await Reservation.bulkWrite(
        bulkOps
    );

};

const promoteWaitlistedReservations = async (
    resourceId,
    bookingDate,
    availableUnits
) => {

    const waitlistedReservations =
    await Reservation.find({

        requestedResource:
            resourceId,

        date:
            bookingDate,

        status:
            "WAITLISTED"

    })
    .sort({

        score: -1,

        createdAt: 1

    });

    const promoted = [];

    for (const reservation of waitlistedReservations) {

        const requiredUnits =
            reservation.quantityRequired || 1;

        if (
            requiredUnits <=
            availableUnits
        ) {

            promoted.push(
                reservation._id
            );

            availableUnits -=
                requiredUnits;

        }

    }

    if (!promoted.length) {
        return;
    }

    await Reservation.updateMany(

        {
            _id: {
                $in: promoted
            }
        },

        {
            $set: {

    allocatedResource:
        resourceId,

    status:
        "APPROVED",

    allocatedAt:
        new Date()

}
        }

    );

};
const rejectExpiredWaitlistedReservations = async () => {

    const now = new Date();

    const waitlistedReservations =
        await Reservation.find({

            status: "WAITLISTED"

        });

    const rejectedIds = [];

    for (const reservation of waitlistedReservations) {

        const bookingDate =
            new Date(reservation.date);

        const actualStart =
            reservation.alternativeStartTime ||
            reservation.startTime;

        const [hour, minute] =
            actualStart.split(":").map(Number);

        bookingDate.setHours(hour, minute, 0, 0);

        if (bookingDate < now) {

            rejectedIds.push(
                reservation._id
            );

        }

    }

    if (!rejectedIds.length)
        return;

    await Reservation.updateMany(

        {

            _id: {

                $in: rejectedIds

            }

        },

        {

            $set: {

                status: "REJECTED"

            }

        }

    );

};

const findAlternativeResource = async (
    reservation,
    resources
) => {

    const alternatives = resources

        .filter(resource => {

            if (
        resource._id.toString() ===
        reservation.requestedResource._id.toString()
    ) {
        return false;
    }

    if (
        resource.category !==
        reservation.requestedResource.category
    ) {
        return false;
    }

            if (
                resource.resourceType ===
                "CAPACITY_BASED"
            ) {

                return (
                    resource.capacity >=
                    reservation.participantCount
                );

            }

            return (

                resource.availableUnits >=
                reservation.quantityRequired

            );

        })

        .sort((a, b) => {

            if (
                a.resourceType ===
                "CAPACITY_BASED"
            ) {

                return (
                    a.capacity -
                    b.capacity
                );

            }

            return (

                a.availableUnits -
                b.availableUnits

            );

        });

    if (
        alternatives.length === 0
    ) {

        return null;

    }

    return alternatives[0];

};
const allocateAlternativeResource =
async (
    reservation,
    resource
) => {

    await Reservation.findByIdAndUpdate(

        reservation._id,

        {
    allocatedResource: resource._id,

    allocationType: "ALTERNATE_RESOURCE",

    status: "ALTERNATIVE_APPROVED",

    allocatedAt: new Date()
}

    );

};

const getAlternativeResourceCandidates =
async (
    reservations
) => {

    return reservations.filter(

        reservation =>

            reservation.status !==
            "APPROVED" &&

            (
                reservation.allocationPreference ==="ALTERNATE_RESOURCE"
            )

    );

};

const getAvailableAlternativeResources =
(
    resources,
    allocatedResources
) => {

    return resources.filter(

        resource =>

            !allocatedResources.has(
                resource._id.toString()
            )

    );

};

const allocateAlternativeResources =
async (

    reservations,

    resources

) => {

    const allocations = [];

    reservations.sort(
        (a, b) => b.score - a.score
    );

    for (
        const reservation
        of reservations
    ) {

        const alternative =
    await findAlternativeResource(
        reservation,
        resources
    );

        if (!alternative) {

            continue;

        }

        allocations.push({

            reservation:
                reservation._id,

            resource:
                alternative._id

        });

        if (
    alternative.resourceType ===
    "CAPACITY_BASED"
) {

    resources =
        resources.filter(
            r =>
                r._id.toString() !==
                alternative._id.toString()
        );

} else {

    alternative.availableUnits -=
        reservation.quantityRequired;

}

    }

    return allocations;

};

const executeAlternativeAllocations =
async (
    allocations
) => {

    if (!allocations.length)
        return;

    const bulkOps =
        allocations.map(
            allocation => ({

                updateOne: {

                    filter: {

                        _id:
                            allocation
                            .reservation

                    },

                    update: {

                       $set:{

    allocatedResource:
        allocation.resource,

    allocationType:
        "ALTERNATE_RESOURCE",

    status:
        "ALTERNATIVE_APPROVED",

    allocatedAt:
        new Date()

}          

                    }

                }

            })
        );

    await Reservation.bulkWrite(
        bulkOps
    );

};

const allocateAlternativeTime = async (
    failures
) => {

    const allocations = [];

    const remainingFailures = [];

    const temporaryAllocations = [];

    for (const item of failures) {

        const reservation =
            item.reservation;

        if (
            reservation.allocationPreference !==
            "ALTERNATE_TIME"
        ) {

            remainingFailures.push(item);

            continue;

        }

        const suggestedTime =
    await findAlternateTime(

        reservation.requestedResource,

        reservation,

        temporaryAllocations

    );

        if (suggestedTime) {

            allocations.push({

                reservation:
                    reservation._id,

                resource:
                    reservation.requestedResource._id,

                alternativeStartTime:
                    suggestedTime

            });
            temporaryAllocations.push({

    resource:
        reservation.requestedResource._id,

    start:
        convertToMinutes(
            suggestedTime
        ),

    end:
        convertToMinutes(
            suggestedTime
        ) +
        reservation.durationHours * 60

});

        }

        else {

            remainingFailures.push(item);

        }

    }

    return {

        allocations,

        remainingFailures

    };

};

const executeAlternativeTimeAllocations =
async (
    allocations
) => {

    if (!allocations.length)
        return;

    const bulkOps =

        allocations.map(
            allocation => ({

                updateOne: {

                    filter: {

                        _id:
                            allocation.reservation

                    },

                    update: {

                        $set: {

    allocatedResource:
        allocation.resource,

    alternativeStartTime:
        allocation.alternativeStartTime,

    allocationType:
        "ALTERNATE_TIME",

    status:
        "ALTERNATIVE_APPROVED",

    allocatedAt:
        new Date()

}

                    }

                }

            })
        );

    await Reservation.bulkWrite(
        bulkOps
    );

};
const runCapacityWorkflow = async (
    reservations,
    resources
) => {

    const {
        allocations,
        failures
    } =
    await allocateCapacityResources(
        reservations,
        resources
    );

    await executeAllocations(
        allocations
    );

    const {
    specificResource,
    alternateResource,
    alternateTime
    } = splitFailedReservations(failures);

    await executeWaitlist(
        specificResource
    );

    const round2Candidates = [

        ...alternateResource

    ];

    const allocatedResourceIds =
        new Set(
            allocations.map(
                allocation =>
                    allocation.resource.toString()
            )
        );

    const availableResources =
    getAvailableAlternativeResources(
        resources,
        allocatedResourceIds
    );

const round2Allocations =
    await allocateAlternativeResources(

        alternateResource.map(item => {

            item.reservation.score =
                item.score;

            return item.reservation;

        }),

        availableResources

    );

await executeAlternativeAllocations(
    round2Allocations
);

    const round2AllocatedIds =
        new Set(
            round2Allocations.map(
                allocation =>
                    allocation.reservation.toString()
            )
        );

    const round2Failures =
        round2Candidates.filter(
            item =>

                !round2AllocatedIds.has(

                    item.reservation._id
                    .toString()

                )
        );

    const round3Candidates = [
        ...alternateTime
    ];

    const resourceOnlyFailures =

        round2Failures.filter(
            item =>
                item.reservation
                .allocationPreference ===
                "ALTERNATE_RESOURCE"
        );

    const {
        allocations:
            round3Allocations,

        remainingFailures

    } =

    await allocateAlternativeTime(
        round3Candidates
    );

    await executeAlternativeTimeAllocations(
        round3Allocations
    );

    await executeWaitlist([

        ...resourceOnlyFailures,

        ...remainingFailures

    ]);

};

const generatePurposeScores = async (
    reservations
) => {

    const pendingReservations =
        reservations.filter(
            reservation =>
                reservation.purposeScore === 0
        );

    if (!pendingReservations.length) {

        return;

    }

    const scores =
        await calculatePurposeScores(
            pendingReservations
        );

        if (scores.length !== pendingReservations.length) {

    throw new Error(
        "Gemini did not return scores for all reservations."
    );

}

    for (const item of scores) {

        const reservation =
            pendingReservations.find(

                reservation =>

                    reservation._id.toString() ===
                    item.reservationId

            );

        if (!reservation) {

    throw new Error(

        `Unknown reservationId returned by Gemini: ${item.reservationId}`

    );

}

        reservation.purposeScore =
            item.score;

        await reservation.save();

    }

};

const runAllocationWorkflow = async (
    reservations,
    resources
) => {

    await generatePurposeScores(
    reservations
);

    const capacityReservations =
        reservations.filter(
            reservation =>
                reservation.requestedResource
                .resourceType ===
                "CAPACITY_BASED"
        );

    const quantityReservations =
        reservations.filter(
            reservation =>
                reservation.requestedResource
                .resourceType ===
                "QUANTITY_BASED"
        );

    if (capacityReservations.length > 0) {

        await runCapacityWorkflow(
            capacityReservations,
            resources
        );

    }
if (quantityReservations.length > 0) {

    const quantityResource =
        quantityReservations[0].requestedResource;

    const {
        allocations,
        failures
    } =
    await allocateQuantityResources(
        quantityReservations,
        quantityResource
    );

    await executeAllocations(
        allocations
    );

    const {
        specificResource,
        alternateTime
    } = splitFailedReservations(
        failures
    );

    await executeWaitlist(
        specificResource
    );

    const {
        allocations: round2Allocations,
        remainingFailures
    } =
    await allocateAlternativeTime(
        alternateTime
    );

    await executeAlternativeTimeAllocations(
        round2Allocations
    );

    await executeWaitlist(
        remainingFailures
    );

}

}

module.exports = {
    calculateCapacityScore,

    calculateFairUsageScore,

    calculateFinalScore,

    allocateCapacityResources,

    executeAllocations,

    allocateQuantityResources,

    executeWaitlist,

    calculateQuantityScore,

    calculateQuantityFinalScore,

    promoteWaitlistedReservations,
    
    findAlternativeResource,
    
    allocateAlternativeResource,
    
    getAlternativeResourceCandidates,

    getAvailableAlternativeResources,

    allocateAlternativeResources,

    executeAlternativeAllocations,
    
    splitFailedReservations,

    allocateAlternativeTime,

    executeAlternativeTimeAllocations,

    runAllocationWorkflow,
    
    runCapacityWorkflow,

    rejectExpiredWaitlistedReservations

};