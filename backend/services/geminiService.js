const { GoogleGenAI } = require("@google/genai");

const ai = new GoogleGenAI({

    apiKey: process.env.GEMINI_API_KEY

});

async function calculatePurposeScores(
    reservations
) {

    const category =
        reservations[0].resourceCategory;

    const requests = reservations.map(

        reservation => ({

            reservationId:
                reservation._id.toString(),

            description:
                reservation.purposeDescription

        })

    );

    const prompt = `

You are an AI evaluator for a University Smart Resource Allocation System.

All of the following reservation requests:

- Belong to the SAME resource category.
- Compete for the SAME booking window.

Your ONLY task is to evaluate and compare the PURPOSE DESCRIPTIONS.

DO NOT consider ANY of the following:

- participant count
- quantity required
- booking duration
- booking time
- resource capacity
- fair usage
- previous reservations
- user history
- allocation preference

These factors are already handled separately by another allocation algorithm.

Evaluation Criteria (highest priority to lowest):

1. Academic examinations, thesis defense, final evaluation, faculty evaluation.
2. Research activities, research proposal discussions, academic presentations.
3. Official university events, departmental seminars, technical workshops.
4. Student club technical events and competitions.
5. Regular club meetings.
6. Personal discussions, informal meetings, casual gatherings.

Instructions:

1. Read ALL purpose descriptions before assigning any score.
2. Compare EVERY reservation with EVERY other reservation.
3. Produce a COMPLETE ranking from highest priority to lowest priority.
4. Use the FULL score range from 0 to 100.
5. The highest-ranked reservation should receive the highest score.
6. The lowest-ranked reservation should receive the lowest score.
7. Unless two descriptions are semantically IDENTICAL, DO NOT assign the same score.
8. Prefer unique scores for every reservation.
9. Every reservationId MUST appear exactly once.
10. Do NOT invent reservationIds.
11. Do NOT omit any reservation.
12. Return ONLY valid JSON.
13. Do NOT include markdown.
14. Do NOT include explanations.
15. Do NOT return any text other than the JSON array.

Return EXACTLY in this format:

[
  {
    "reservationId": "abc123",
    "score": 96
  },
  {
    "reservationId": "abc124",
    "score": 88
  }
]

Reservations:

${JSON.stringify(requests, null, 2)}

`;

    const response =
        await ai.models.generateContent({

            model: "gemini-2.5-flash-lite",

            contents: prompt

        });

    const text =
        response.text
            .replace(/```json/g, "")
            .replace(/```/g, "")
            .trim();

    try {

           const scores = JSON.parse(text);

    console.log("================================");
    console.log("Gemini Response:");
    console.log(scores);
    console.log("================================");

    return scores;

    } catch (error) {

        console.error("Gemini returned:");

        console.error(text);

        throw new Error(
            "Invalid JSON returned by Gemini"
        );

    }

}

module.exports = {

    calculatePurposeScores

};