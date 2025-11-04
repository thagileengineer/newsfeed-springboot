import fs from "fs";
import path from "path";

const FILE_PATH = path.resolve("./users.csv");
const API_URL = "http://localhost:8080/users/register";

const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function bulkRegister() {
  try {
    const data = fs.readFileSync(FILE_PATH, "utf8");
    const lines = data.trim().split("\n").slice(1);

    for (const [index, line] of lines.entries()) {
      const [username, email, firstName, password] = line
        .split(",")
        .map((p) => p.replace(/(^"|"$)/g, "").trim());

      const payload = {
        username,
        email,
        firstName,
        password,
      };

      console.log(`➡️  [${index + 1}] Registering user: ${username}`, payload);

      try {
        const res = await fetch(API_URL, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });

        if (!res.ok) {
          const text = await res.text();
          console.error(`❌ Failed: ${res.status} ${res.statusText} → ${text}`);
        } else {
          console.log(`✅ Success: ${username}`);
        }
      } catch (error) {
        console.error(`⚠️  Network error for ${username}:`, error.message);
      }

      await delay(100);
    }

    console.log("\n🎉 All users processed!");
  } catch (error) {
    console.error("Failed to read file.", error);
  }
}

bulkRegister();
