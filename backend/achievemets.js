const admin = require('firebase-admin');
const serviceAccount = require("../config/serviceAccountKey.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();




    achievements = [
        {
            id: 'first_steps',
            name: 'First Steps',
            description: 'Complete your first activity',
            requiredProgress: 1,
            finishCount: 0
        },
        {
            id: 'first_workout',
            name: 'Complete 5 workouts',
            description: 'Complete 5 workouts',
            requiredProgress: 5,
            finishCount: 0
        },
        {
            id: 'second_workout',
            name: 'Getting into the swing of things',
            description: 'Complete 10 workouts',
            requiredProgress: 10,
            finishCount: 0
        }
    ];

async function addAchievements() {
    const batch = db.batch();

    achievements.forEach(achievement => {
        const docRef = db.collection('achievements').doc(achievement.id);
        batch.set(docRef, achievement, { merge: true });
    });

    try {
        await batch.commit();
        console.log('Achievements added/updated successfully.');
        process.exit(0);
    } catch (error) {
        console.error('Error adding achievements:', error);
        process.exit(1);
    }
}

addAchievements();