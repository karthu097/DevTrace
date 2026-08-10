# 3-Minute Live Demo Script

Follow this script to demonstrate DevTrace effectively to a hiring manager, team, or audience.

## Setup
Ensure DevTrace is running via `docker-compose.prod.yml`.
Open `http://localhost:3000`.

## Step 1: The Healthy System
1. Go to the **Dashboard**.
2. Point out the healthy metrics (High request volume, low error rate, sub-200ms latency).
3. Switch to the **Dependencies** tab.
4. Point out the green nodes. Explain: "Here is our live microservice topology. Everything is flowing smoothly."

## Step 2: Triggering the Failure
1. Say: "Now, let's pretend it's 2 AM on Black Friday, and a critical upstream dependency fails."
2. Open a terminal and run the Incident Generator:
   ```bash
   curl -X POST http://localhost:8085/api/demo/incidents/payment-timeout
   ```
3. Switch back to the **Dashboard**. Watch the "Active Incidents" feed instantly populate with a Critical Incident. The Error Rate metric will spike.

## Step 3: The Investigation
1. Say: "Normally, an engineer would have to query logs for the next hour. Let's click the trace ID in DevTrace."
2. **Trace Waterfall:** Show the waterfall. "You can see the request enters the API Gateway, hits the Order Service, but then the Payment Service hangs."
3. **Deterministic RCA:** Point to the red box. "DevTrace's algorithm walked the critical path and isolated the exact point of failure: The Payment Provider timed out after 5000ms."
4. **AI Investigator:** Point to the AI panel. "And instead of reading JSON, the AI has generated a human-readable summary of exactly what happened, proving that the third-party payment gateway is to blame, and recommending we check their status page."

## Step 4: Conclusion
"DevTrace took what used to be a 1-hour war-room investigation and solved it in 5 seconds."
