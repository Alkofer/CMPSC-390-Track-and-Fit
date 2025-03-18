const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();


exports.createUser = functions.https.onRequest(async (req, res) => {
  if (req.method !== "POST") {
    return res.status(405).send("Method Not Allowed");
  }

  const { email, password, firstName, lastName, dob } = req.body;

  try {
    const userRecord = await admin.auth().createUser({ email, password });
    const createdAt = admin.firestore.Timestamp.now();

    await admin.firestore().collection("users").doc(userRecord.uid).set({
      firstName: firstName || "",
      lastName: lastName || "",
      dob: dob || "",
      createdAt: createdAt
    });

    const achievementsSnapshot = await admin.firestore().collection("achievements").get();
    const batch = admin.firestore().batch();

    achievementsSnapshot.forEach(doc => {
      const achievementData = doc.data();
      const userAchievement = {
        name: achievementData.name,
        complete: false,
        progress: 0,
        finishCount: 0
      };

      const userAchievementRef = admin.firestore()
          .collection("users")
          .doc(userRecord.uid)
          .collection("achievements")
          .doc(doc.id);

      batch.set(userAchievementRef, userAchievement);
    });

    await batch.commit();

    return res.status(200).json({
      uid: userRecord.uid,
      message: "User created and achievements seeded successfully"
    });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
});

exports.loginUser = functions.https.onRequest(async (req, res) => {
  if (req.method !== "POST") {
    return res.status(405).send("Method Not Allowed");
  }

  const { email, password } = req.body;
  if (!email || !password) {
    return res
        .status(400)
        .json({ error: "Email and password are required." });
  }

  const apiKey = process.env.API_KEY;

  try {
    const response = await fetch(
        `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${apiKey}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            email,
            password,
            returnSecureToken: true,
          }),
        }
    );
    const data = await response.json();

    if (data.error) {
      return res.status(400).json({ error: data.error.message });
    }

    return res.status(200).json({
      uid: data.localId,
      idToken: data.idToken,
      refreshToken: data.refreshToken,
      message: "Login successful",
    });
  } catch (error) {
    console.error("Error during login:", error);
    return res.status(500).json({ error: error.message });
  }
});


exports.getWorkoutRoutine = functions.https.onRequest(async (req, res) => {
  if (req.method !== "POST") {
    return res.status(405).send("Method Not Allowed");
  }
  const { userId, workoutType } = req.body;
  if (!userId || !workoutType) {
    return res
        .status(400)
        .json({ error: "userId and workoutType are required." });
  }

  const numWorkouts = Math.floor(Math.random() * 4) + 3;

  try {
    const userDoc = await admin
        .firestore()
        .collection("users")
        .doc(userId)
        .get();
    let blacklisted = [];
    if (userDoc.exists) {
      const userData = userDoc.data();
      blacklisted = userData.blacklistedWorkouts || [];
    }

    const snapshot = await admin
        .firestore()
        .collection("workouts")
        .where("muscleGroup", "==", workoutType)
        .get();

    let availableWorkouts = [];
    snapshot.forEach((doc) => {
      if (!blacklisted.includes(doc.id)) {
        availableWorkouts.push({ id: doc.id, ...doc.data() });
      }
    });

    if (availableWorkouts.length === 0) {
      return res
          .status(404)
          .json({ error: "No available workouts found." });
    }

    availableWorkouts.sort(() => 0.5 - Math.random());

    const selectedWorkouts = availableWorkouts.slice(
        0,
        Math.min(numWorkouts, availableWorkouts.length)
    );

    return res.status(200).json({ workouts: selectedWorkouts });
  } catch (error) {
    console.error("Error retrieving workouts:", error);
    return res.status(500).json({ error: error.message });
  }
});


exports.getUserAchievements = functions.https.onRequest(async (req, res) => {
  try {
    const userId = req.query.userId || req.body.userId;
    if (!userId) {
      return res.status(400).json({ error: 'Missing userId parameter' });
    }

    const achievementsRef = admin.firestore()
        .collection('users')
        .doc(userId)
        .collection('achievements');

    const snapshot = await achievementsRef.get();
    const achievements = [];

    snapshot.forEach(doc => {
      achievements.push({ id: doc.id, ...doc.data() });
    });

    return res.status(200).json({ achievements });
  } catch (error) {
    console.error('Error fetching achievements:', error);
    return res.status(500).json({ error: 'Internal Server Error' });
  }
});


exports.updateUserAchievement = functions.https.onRequest(async (req, res) => {
  try {
    const { userId, achievementId, progress, complete, finishCount } = req.body;

    if (!userId || !achievementId) {
      return res.status(400).json({ error: 'Missing userId or achievementId' });
    }

    const updateData = {};
    if (progress !== undefined) updateData.progress = progress;
    if (complete !== undefined) updateData.complete = complete;
    if (finishCount !== undefined) updateData.finishCount = finishCount;

    const achievementRef = admin.firestore()
        .collection('users')
        .doc(userId)
        .collection('achievements')
        .doc(achievementId);

    await achievementRef.set(updateData, { merge: true });
    return res.status(200).json({ message: 'Achievement updated successfully' });
  } catch (error) {
    console.error('Error updating achievement:', error);
    return res.status(500).json({ error: 'Internal Server Error' });
  }
});





















exports.helloWorld = functions.https.onRequest((req, res) => {
  res.send("Hello, world!");
});