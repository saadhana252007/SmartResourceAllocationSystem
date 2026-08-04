require("dotenv").config();

const { GoogleGenAI } = require("@google/genai");

const ai = new GoogleGenAI({
    apiKey: process.env.GEMINI_API_KEY
});

async function test() {

    const models = [

        "gemini-3.6-flash",
        "gemini-3.5-flash",
        "gemini-3.1-flash-lite",
        "gemini-flash-latest",
        "gemini-2.0-flash"

    ];

    for (const model of models) {

        try {

            const response =
                await ai.models.generateContent({

                    model,

                    contents: "Reply with only OK"

                });

            console.log("✅ WORKS:", model);
            console.log(response.text);
            return;

        } catch (e) {

            console.log("❌ FAILED:", model);
            console.log(e.message);

        }

    }

}

test();