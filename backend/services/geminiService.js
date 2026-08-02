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

You are an AI system assisting a University Smart Resource Allocation System.

All of the following reservations belong to:

Resource Category:
${category}

They are competing for the SAME booking window.

Your responsibility is ONLY to evaluate the purpose descriptions.

DO NOT consider:

- participant count
- quantity required
- duration
- booking time
- fair usage
- previous reservations
- resource capacity

Those factors are already handled by another allocation algorithm.

Instructions:

1. Read every purpose description carefully.
2. Compare every reservation with all the others.
3. Rank the reservations from highest priority to lowest priority.
4. Based on the ranking, assign each reservation a relative importance score between 0 and 100.
5. If two descriptions have exactly the same meaning, they may receive the same score.
6. Every reservationId MUST appear exactly once.
7. Do NOT invent reservationIds.
8. Do NOT omit any reservation.
9. Return ONLY valid JSON.
10. Do NOT include markdown.
11. Do NOT include explanations.

Return this exact format:

[
  {
    "reservationId":"abc123",
    "score":91
  },
  {
    "reservationId":"abc124",
    "score":73
  }
]

Reservations:

${JSON.stringify(requests, null, 2)}

`;

    const response =
        await ai.models.generateContent({

            model: "gemini-2.5-flash",

            contents: prompt

        });

    const text =
        response.text
            .replace(/```json/g, "")
            .replace(/```/g, "")
            .trim();

    try {

        return JSON.parse(text);

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