const Reservation = require("../models/Reservation");

const calculateCapacityScore = (
    resourceCapacity,
    participantCount
) => {

    if (participantCount > resourceCapacity) {
        return 0;
    }

    return (
        participantCount / resourceCapacity
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
            updatedAt: {
                $gte: thirtyDaysAgo
            }
        });

    return Math.max(
        0,
        100 - recentAllocations * 5
    );

};

const calculatePurposeScore = (
    purpose
) => {

    const purposeWeights = {

        "Academic": 100,

        "Research": 90,

        "Project Work": 80,

        "Club Activity": 70,

        "Personal": 50

    };

    return (
        purposeWeights[purpose] || 50
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
        calculatePurposeScore(
            reservation.purpose
        );

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

                            status:
                                "APPROVED"
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
        calculatePurposeScore(
            reservation.purpose
        );

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
        (a, b) => b.score - a.score
    );

    let remainingUnits =
        resource.availableUnits;

    const allocations = [];

    const failures = [];

    for (const item of scoredReservations) {

        const requiredUnits =
            item.reservation.quantityRequired;

        if (
            remainingUnits >= requiredUnits
        ) {

            allocations.push({

                reservation:
                    item.reservation._id,

                resource:
                    resource._id,

                score:
                    item.score

            });

            remainingUnits -=
                requiredUnits;

        } else {

            failures.push({
                reservation:item.reservation,
                resource:resource._id,
                score:item.score
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

    const alternateResourceAndTime = [];

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

        else if (
            preference ===
            "ALTERNATE_TIME"
        ) {

            alternateTime.push(
                item
            );

        }

        else {

            alternateResourceAndTime.push(
                item
            );

        }

    }

    return {

        specificResource,

        alternateResource,

        alternateTime,

        alternateResourceAndTime

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
        .sort({ score: -1 });

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
                    "APPROVED"

            }
        }

    );

};

const findAlternativeResource = async (
    reservation,
    resources
) => {

    const alternatives =
        resources

        .filter(resource =>

            resource.category ===
            reservation.requestedResource.category &&

            resource.capacity >=
            reservation.participantCount

        )

        .sort(
            (a, b) =>
                a.capacity - b.capacity
        );

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

            allocatedResource:
                resource._id,

            status:
                "ALTERNATIVE_APPROVED"

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
                reservation
                .allocationPreference ===
                "ALTERNATE_RESOURCE"

                ||

                reservation
                .allocationPreference ===
                "ALTERNATE_RESOURCE_AND_TIME"
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
            resources.find(

                resource =>

                    resource.category ===

                    reservation
                    .requestedResource
                    .category

                    &&

                    resource.capacity >=

                    reservation
                    .participantCount

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

        resources =
            resources.filter(

                r =>

                    r._id.toString()

                    !==

                    alternative._id
                    .toString()

            );

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

                        $set: {

                            allocatedResource:

                                allocation
                                .resource,

                            status:

                                "ALTERNATIVE_APPROVED"

                        }

                    }

                }

            })
        );

    await Reservation.bulkWrite(
        bulkOps
    );

};

const allocateAlternativeTime =
async (
    failures
) => {

    const allocations = [];

    const remainingFailures = [];

    for (const item of failures) {

        const preference =
            item.reservation
            .allocationPreference;

        if (

            preference !==
            "ALTERNATE_TIME"

            &&

            preference !==
            "ALTERNATE_RESOURCE_AND_TIME"

        ) {

            remainingFailures.push(
                item
            );

            continue;

        }

        allocations.push({

            reservation:
                item.reservation._id

        });

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
                            allocation
                            .reservation

                    },

                    update: {

                        $set: {

                            status:

                            "ALTERNATIVE_APPROVED"

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
        alternateTime,
        alternateResourceAndTime
    } =
    splitFailedReservations(
        failures
    );

    await executeWaitlist(
        specificResource
    );

    const round2Candidates = [

        ...alternateResource,

        ...alternateResourceAndTime

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

        round2Candidates.map(
            item => {

                item.reservation.score =
                    item.score;

                return item.reservation;

            }
        ),

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

        ...alternateTime,

        ...round2Failures.filter(
            item =>
                item.reservation
                .allocationPreference ===
                "ALTERNATE_RESOURCE_AND_TIME"
        )

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

const runAllocationWorkflow = async (
    reservations,
    resources
) => {

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
        quantityReservations[0]
        .requestedResource;

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

    await executeWaitlist(
        failures
    );

}

};
module.exports = {
    calculateCapacityScore,

    calculateFairUsageScore,

    calculatePurposeScore,

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
    
    runCapacityWorkflow

};