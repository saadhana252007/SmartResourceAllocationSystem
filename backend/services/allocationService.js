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
function convertTimeToMinutes(time) {

    const [hours, minutes] = time
        .split(":")
        .map(Number);

    return (hours * 60) + minutes;

}

function hasTimeConflict(existingStart, existingEnd, newStart, newEnd) {

    return newStart < existingEnd &&
           existingStart < newEnd;

}

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

    const allocatedSlots = {};

    for (const item of scoredReservations) {

        const reservation =
            item.reservation;

        const resource =
            resources.find(
                resource =>
                    resource._id.toString() ===
                    reservation.requestedResource._id.toString()
            );

        if (
            !resource ||
            resource.capacity <
                reservation.participantCount
        ) {

            failures.push({
                reservation,
                score: item.score
            });

            continue;

        }

        const resourceId =
            resource._id.toString();

        if (!allocatedSlots[resourceId]) {

            allocatedSlots[resourceId] = [];

        }
        if (allocatedSlots[resourceId].length === 0) {

    const existingReservations =
        await Reservation.find({

            requestedResource: resource._id,

            date: reservation.date,

            status: {
                $in: [
                    "APPROVED",
                    "ALTERNATIVE_APPROVED"
                ]
            }

        });

    for (const existing of existingReservations) {

        const actualStart =
            existing.alternativeStartTime ||
            existing.startTime;

        allocatedSlots[resourceId].push({

            start:
                convertTimeToMinutes(actualStart),

            end:
                convertTimeToMinutes(actualStart) +
                existing.durationHours * 60

        });

    }

}

        const startMinutes =
            convertTimeToMinutes(
                reservation.startTime
            );

        const endMinutes =
            startMinutes +
            (reservation.durationHours * 60);

        let conflict = false;

        for (const slot of allocatedSlots[resourceId]) {

            if (
                hasTimeConflict(
                    slot.start,
                    slot.end,
                    startMinutes,
                    endMinutes
                )
            ) {

                conflict = true;
                break;

            }

        }

        if (conflict) {

            failures.push({

                reservation,

                score: item.score

            });

            continue;

        }

        allocations.push({

            reservation:
                reservation._id,

            resource:
                resource._id,

            score:
                item.score

        });

        allocatedSlots[resourceId].push({

            start: startMinutes,

            end: endMinutes

        });

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

            requestedResource: resourceId,

            date: bookingDate,

            status: "WAITLISTED"

        }).sort({

            score: -1,

            createdAt: 1

        });

    const existingReservations =
        await Reservation.find({

            requestedResource: resourceId,

            date: bookingDate,

            status: {

                $in: [

                    "APPROVED",

                    "ALTERNATIVE_APPROVED"

                ]

            }

        });

    const occupiedSlots = [];

    for (const reservation of existingReservations) {

        const actualStart =
            reservation.alternativeStartTime ||
            reservation.startTime;

        occupiedSlots.push({

            start:
                convertToMinutes(actualStart),

            end:
                convertToMinutes(actualStart) +
                reservation.durationHours * 60,

            units:
                reservation.quantityRequired || 1

        });

    }

    const promoted = [];

    for (const reservation of waitlistedReservations) {

        const requiredUnits =
            reservation.quantityRequired || 1;

        const start =
            convertToMinutes(
                reservation.startTime
            );

        const end =
            start +
            reservation.durationHours * 60;

        let usedUnits = 0;

        for (const slot of occupiedSlots) {

            if (

                hasTimeConflict(

                    slot.start,

                    slot.end,

                    start,

                    end

                )

            ) {

                usedUnits += slot.units;

            }

        }

        if (

            availableUnits - usedUnits >=
            requiredUnits

        ) {

            promoted.push(reservation._id);

            occupiedSlots.push({

                start,

                end,

                units: requiredUnits

            });

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
    resources,
    temporaryAllocations = []
) => {

    const requestStart =
        convertToMinutes(
            reservation.startTime
        );

    const requestEnd =
        requestStart +
        reservation.durationHours * 60;

    const alternatives = [];

    for (const resource of resources) {

        if (
            resource._id.toString() ===
            reservation.requestedResource._id.toString()
        ) {
            continue;
        }

        if (
            resource.category !==
            reservation.requestedResource.category
        ) {
            continue;
        }

        if (
            resource.resourceType ===
            "CAPACITY_BASED"
        ) {

            if (
                resource.capacity <
                reservation.participantCount
            ) {
                continue;
            }

        } else {

            if (
                resource.availableUnits <
                reservation.quantityRequired
            ) {
                continue;
            }

        }

        const existingReservations =
            await Reservation.find({

                requestedResource: resource._id,

                date: reservation.date,

                status: {

                    $in: [

                        "APPROVED",

                        "ALTERNATIVE_APPROVED"

                    ]

                }

            });

        let conflict = false;

        for (const existing of existingReservations) {

            const existingStart =
                convertToMinutes(

                    existing.alternativeStartTime ||

                    existing.startTime

                );

            const existingEnd =
                existingStart +
                existing.durationHours * 60;

            if (
                hasTimeConflict(

                    existingStart,

                    existingEnd,

                    requestStart,

                    requestEnd

                )
            ) {

                conflict = true;

                break;

            }

        }



        if (!conflict) {

            for (const allocation of temporaryAllocations) {

                if (

                    allocation.resource.toString() !==
                    resource._id.toString()

                ) {
                    continue;
                }

                if (

                    hasTimeConflict(

                        allocation.start,

                        allocation.end,

                        requestStart,

                        requestEnd

                    )

                ) {

                    conflict = true;

                    break;

                }

            }

        }

        if (!conflict) {

            alternatives.push(resource);

        }

    }

    if (!alternatives.length) {

        return null;

    }

    alternatives.sort((a, b) => {

        if (

            a.resourceType ===
            "CAPACITY_BASED"

        ) {

            return a.capacity - b.capacity;

        }

        return (

            a.availableUnits -
            b.availableUnits

        );

    });

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

    const temporaryAllocations = [];


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
        resources,
        temporaryAllocations
    );
    const requestStart =
    convertToMinutes(
        reservation.startTime
    );

const requestEnd =
    requestStart +
    reservation.durationHours * 60;

        if (!alternative) {

            continue;

        }

        allocations.push({

            reservation:
                reservation._id,

            resource:
                alternative._id

        });
        temporaryAllocations.push({

    resource:
        alternative._id,

    start:
        requestStart,

    end:
        requestEnd

});

if (
    alternative.resourceType ===
    "QUANTITY_BASED"
) {

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


const round2Allocations =
    await allocateAlternativeResources(

        alternateResource.map(item => {

            item.reservation.score =
                item.score;

            return item.reservation;

        }),

        resources

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
                reservation.purposeScoreStatus ===
                "PENDING"
        );

    if (!pendingReservations.length) {

        return;

    }

    let scores;

    try {

        scores =
            await calculatePurposeScores(
                pendingReservations
            );

    }

    catch (error) {

        console.log(
            "Gemini unavailable."
        );

        await Reservation.updateMany(

            {

                _id: {

                    $in:
                        pendingReservations.map(

                            reservation =>
                                reservation._id

                        )

                }

            },

            {

                $set: {

                    purposeScoreStatus:
                        "FAILED"

                }

            }

        );

        return;

    }

    if (
        scores.length !==
        pendingReservations.length
    ) {

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

        reservation.purposeScoreStatus =
            "COMPLETED";

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

    const groupedQuantityReservations = {};

    for (const reservation of quantityReservations) {

        const resourceId =
            reservation.requestedResource._id.toString();

        if (!groupedQuantityReservations[resourceId]) {

            groupedQuantityReservations[resourceId] = [];

        }

        groupedQuantityReservations[resourceId].push(
            reservation
        );

    }

    for (const resourceId of Object.keys(groupedQuantityReservations)) {

        const reservationsForResource =
            groupedQuantityReservations[resourceId];

        const quantityResource =
            reservationsForResource[0].requestedResource;

        const {
            allocations,
            failures
        } =
        await allocateQuantityResources(
            reservationsForResource,
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