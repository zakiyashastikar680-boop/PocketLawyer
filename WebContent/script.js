/* =========================================
   POCKET LAWYER AI
   FRONTEND JAVASCRIPT
========================================= */


/* =========================================
   LEGAL PROBLEM ANALYZER
========================================= */

async function analyzeProblem() {

    const problem =
        document.getElementById("problem").value;

    const category =
        document.getElementById("category").value;

    const state =
        document.getElementById("state").value;


    if (problem.trim() === "") {

        alert("Please describe your legal problem.");

        return;
    }


    const result =
        document.getElementById("result");


    result.style.display = "block";


    document.getElementById("resultCategory")
        .innerText = "Analyzing...";


    document.getElementById("resultExplanation")
        .innerText =
        "Pocket Lawyer is analyzing the information you provided.";


    try {

        const response = await fetch(
            "http://localhost:8080/api/analyze",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    problem: problem,
                    category: category,
                    state: state
                })
            }
        );


        if (!response.ok) {

            throw new Error("Server error");

        }


        const data = await response.json();


        document.getElementById("resultCategory")
            .innerText = data.category;


        document.getElementById("resultExplanation")
            .innerText = data.explanation;


        const documents =
            document.getElementById("documents");


        documents.innerHTML = "";


        if (data.documents &&
            data.documents.length > 0) {

            data.documents.forEach(function(item) {

                const li =
                    document.createElement("li");

                li.innerText = item;

                documents.appendChild(li);

            });

        } else {

            const li =
                document.createElement("li");

            li.innerText =
                "Relevant documents may depend on your situation.";

            documents.appendChild(li);

        }


        result.scrollIntoView({
            behavior: "smooth"
        });


    } catch (error) {

        console.error(error);


        document.getElementById("resultCategory")
            .innerText = category;


        document.getElementById("resultExplanation")
            .innerText =
            "The Java server could not be reached. Please start PocketLawyerServer in Eclipse and try again.";


        document.getElementById("documents")
            .innerHTML =
            "<li>Start the Java backend to receive the demo analysis.</li>";

    }

}


/* =========================================
   DOCUMENT ANALYZER
========================================= */

const documentInput =
    document.getElementById("documentFile");


if (documentInput) {

    documentInput.addEventListener(
        "change",
        function() {

            const file =
                this.files[0];

            const fileName =
                document.getElementById("fileName");


            if (file) {

                fileName.innerText =
                    "Selected: " + file.name;

            } else {

                fileName.innerText =
                    "No file selected";

            }

        }
    );

}


function analyzeDocument() {

    const file =
        document.getElementById("documentFile").files[0];


    const result =
        document.getElementById("documentResult");


    if (!file) {

        result.innerHTML = `
            <div class="empty-document">
                <div>⚠️</div>
                <h3>Please Select a Document</h3>
                <p>
                    Choose a PDF or document before starting analysis.
                </p>
            </div>
        `;

        return;

    }


    result.innerHTML = `

        <div class="result-box">

            <div class="result-header">

                <div>

                    <p class="small-title">
                        DOCUMENT ANALYSIS
                    </p>

                    <h2>📄 ${escapeHTML(file.name)}</h2>

                </div>

                <div class="result-check">
                    ✓
                </div>

            </div>


            <div class="result-content">

                <h3>🔎 Document Received</h3>

                <p>
                    Your document has been successfully selected
                    for the Pocket Lawyer prototype.
                </p>


                <h3>📋 Prototype Analysis</h3>

                <ul>

                    <li>Document type detected</li>

                    <li>Important sections can be extracted</li>

                    <li>Key dates and terms can be identified</li>

                    <li>Potentially important clauses can be highlighted</li>

                </ul>

            </div>


            <div class="notice">

                This prototype demonstrates the document-analysis
                workflow. Actual legal interpretation should be
                verified by a qualified professional.

            </div>

        </div>

    `;

}


/* =========================================
   ROADMAP PROGRESS
========================================= */

function updateRoadmapProgress() {

    const checks =
        document.querySelectorAll(".roadmap-check");


    if (checks.length === 0) {

        return;

    }


    let completed = 0;


    checks.forEach(function(check) {

        if (check.checked) {

            completed++;

        }

    });


    const percentage =
        Math.round(
            (completed / checks.length) * 100
        );


    const progressFill =
        document.getElementById("progressFill");


    const progressText =
        document.getElementById("progressText");


    const progressPercent =
        document.getElementById("progressPercent");


    if (progressFill) {

        progressFill.style.width =
            percentage + "%";

    }


    if (progressText) {

        progressText.innerText =
            completed +
            " of " +
            checks.length +
            " steps completed";

    }


    if (progressPercent) {

        progressPercent.innerText =
            percentage + "%";

    }

}


document.querySelectorAll(".roadmap-check")
    .forEach(function(check) {

        check.addEventListener(
            "change",
            updateRoadmapProgress
        );

    });


/* =========================================
   SECURITY HELPER
========================================= */

function escapeHTML(value) {

    return value
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");

}