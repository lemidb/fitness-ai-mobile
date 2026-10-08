package com.example.fitnessai.model

data class ExerciseDef(
    val name: String,
    val muscleGroup: MuscleGroup,
    val exerciseType: ExerciseType = ExerciseType.STRENGTH,
    val aliases: List<String> = emptyList(),
    val defaultWeightKg: Float = 50f,
    val defaultSets: Int = 3,
    val defaultReps: Int = 10
)

object ExerciseLibrary {
    val allExercises: List<ExerciseDef> = listOf(
        // Chest
        ExerciseDef("Bench Press", MuscleGroup.CHEST, ExerciseType.STRENGTH, listOf("flat bench", "barbell bench", "chest press"), 70f, 4, 8),
        ExerciseDef("Incline Bench Press", MuscleGroup.CHEST, ExerciseType.STRENGTH, listOf("incline dumbbell", "incline press"), 60f, 4, 8),
        ExerciseDef("Dumbbell Fly", MuscleGroup.CHEST, ExerciseType.STRENGTH, listOf("db fly", "chest fly"), 16f, 3, 12),
        ExerciseDef("Cable Crossover", MuscleGroup.CHEST, ExerciseType.STRENGTH, listOf("cable fly", "crossover"), 20f, 3, 12),
        ExerciseDef("Push-ups", MuscleGroup.CHEST, ExerciseType.STRENGTH, listOf("pushup", "press up"), 0f, 3, 15),
        ExerciseDef("Dips (Chest Focus)", MuscleGroup.CHEST, ExerciseType.STRENGTH, listOf("bodyweight dip", "chest dip"), 0f, 3, 10),

        // Back
        ExerciseDef("Deadlift", MuscleGroup.BACK, ExerciseType.STRENGTH, listOf("conventional deadlift", "barbell deadlift"), 100f, 4, 5),
        ExerciseDef("Barbell Row", MuscleGroup.BACK, ExerciseType.STRENGTH, listOf("bent over row", "pendlay row"), 60f, 4, 8),
        ExerciseDef("Pull-ups", MuscleGroup.BACK, ExerciseType.STRENGTH, listOf("pullup", "wide grip pullup"), 0f, 3, 8),
        ExerciseDef("Lat Pulldown", MuscleGroup.BACK, ExerciseType.STRENGTH, listOf("cable pulldown", "lat pull"), 55f, 3, 10),
        ExerciseDef("Seated Cable Row", MuscleGroup.BACK, ExerciseType.STRENGTH, listOf("cable row", "low row"), 50f, 3, 10),
        ExerciseDef("T-Bar Row", MuscleGroup.BACK, ExerciseType.STRENGTH, listOf("t bar", "corner row"), 45f, 3, 10),

        // Legs
        ExerciseDef("Squat", MuscleGroup.LEGS, ExerciseType.STRENGTH, listOf("back squat", "barbell squat"), 90f, 4, 6),
        ExerciseDef("Front Squat", MuscleGroup.LEGS, ExerciseType.STRENGTH, listOf("olympic squat", "front rack squat"), 70f, 3, 8),
        ExerciseDef("Leg Press", MuscleGroup.LEGS, ExerciseType.STRENGTH, listOf("45 deg press", "machine leg press"), 140f, 4, 10),
        ExerciseDef("Romanian Deadlift", MuscleGroup.LEGS, ExerciseType.STRENGTH, listOf("rdl", "stiff leg deadlift"), 70f, 3, 10),
        ExerciseDef("Lunges", MuscleGroup.LEGS, ExerciseType.STRENGTH, listOf("walking lunges", "db lunge"), 20f, 3, 12),
        ExerciseDef("Leg Curl", MuscleGroup.LEGS, ExerciseType.STRENGTH, listOf("hamstring curl", "lying leg curl"), 40f, 3, 12),
        ExerciseDef("Leg Extension", MuscleGroup.LEGS, ExerciseType.STRENGTH, listOf("quad extension", "machine extension"), 45f, 3, 12),
        ExerciseDef("Calf Raise", MuscleGroup.LEGS, ExerciseType.STRENGTH, listOf("standing calf", "seated calf raise"), 60f, 4, 15),

        // Shoulders
        ExerciseDef("Overhead Press", MuscleGroup.SHOULDERS, ExerciseType.STRENGTH, listOf("military press", "ohp", "shoulder press"), 45f, 4, 6),
        ExerciseDef("Lateral Raise", MuscleGroup.SHOULDERS, ExerciseType.STRENGTH, listOf("side raise", "dumbbell lateral"), 10f, 4, 12),
        ExerciseDef("Front Raise", MuscleGroup.SHOULDERS, ExerciseType.STRENGTH, listOf("dumbbell front raise", "plate raise"), 10f, 3, 12),
        ExerciseDef("Face Pull", MuscleGroup.SHOULDERS, ExerciseType.STRENGTH, listOf("cable face pull", "rear delt pull"), 25f, 3, 15),
        ExerciseDef("Arnold Press", MuscleGroup.SHOULDERS, ExerciseType.STRENGTH, listOf("rotating shoulder press", "arnold db press"), 18f, 3, 10),
        ExerciseDef("Rear Delt Fly", MuscleGroup.SHOULDERS, ExerciseType.STRENGTH, listOf("reverse fly", "reverse pec deck"), 12f, 3, 15),

        // Arms
        ExerciseDef("Barbell Curl", MuscleGroup.ARMS, ExerciseType.STRENGTH, listOf("bicep curl", "straight bar curl"), 30f, 3, 10),
        ExerciseDef("Hammer Curl", MuscleGroup.ARMS, ExerciseType.STRENGTH, listOf("neutral curl", "dumbbell hammer"), 14f, 3, 12),
        ExerciseDef("Tricep Dip", MuscleGroup.ARMS, ExerciseType.STRENGTH, listOf("parallel bar dip", "bench dip"), 0f, 3, 10),
        ExerciseDef("Skull Crusher", MuscleGroup.ARMS, ExerciseType.STRENGTH, listOf("lying tricep extension", "french press"), 25f, 3, 10),
        ExerciseDef("Cable Curl", MuscleGroup.ARMS, ExerciseType.STRENGTH, listOf("rope bicep curl", "low pulley curl"), 25f, 3, 12),
        ExerciseDef("Tricep Pushdown", MuscleGroup.ARMS, ExerciseType.STRENGTH, listOf("rope pushdown", "straight bar pushdown"), 30f, 3, 12),
        ExerciseDef("Preacher Curl", MuscleGroup.ARMS, ExerciseType.STRENGTH, listOf("ez bar preacher", "machine curl"), 25f, 3, 10),

        // Core
        ExerciseDef("Plank", MuscleGroup.CORE, ExerciseType.STRENGTH, listOf("front plank", "forearm plank"), 0f, 3, 60),
        ExerciseDef("Crunches", MuscleGroup.CORE, ExerciseType.STRENGTH, listOf("ab crunch", "situps"), 0f, 3, 20),
        ExerciseDef("Russian Twist", MuscleGroup.CORE, ExerciseType.STRENGTH, listOf("seated twist", "weighted twist"), 10f, 3, 20),
        ExerciseDef("Leg Raise", MuscleGroup.CORE, ExerciseType.STRENGTH, listOf("hanging leg raise", "captains chair"), 0f, 3, 12),
        ExerciseDef("Ab Wheel Rollout", MuscleGroup.CORE, ExerciseType.STRENGTH, listOf("ab roller", "rollout"), 0f, 3, 10),
        ExerciseDef("Cable Woodchopper", MuscleGroup.CORE, ExerciseType.STRENGTH, listOf("woodchop", "diagonal cable twist"), 20f, 3, 12),

        // Cardio
        ExerciseDef("Treadmill Run", MuscleGroup.FULL_BODY, ExerciseType.CARDIO, listOf("running", "jogging", "sprints"), 0f, 1, 30),
        ExerciseDef("Cycling", MuscleGroup.LEGS, ExerciseType.CARDIO, listOf("stationary bike", "spin", "bike"), 0f, 1, 45),
        ExerciseDef("Rowing Machine", MuscleGroup.FULL_BODY, ExerciseType.CARDIO, listOf("ergometer", "rower"), 0f, 1, 20),
        ExerciseDef("Elliptical", MuscleGroup.FULL_BODY, ExerciseType.CARDIO, listOf("cross trainer", "elliptical trainer"), 0f, 1, 30),
        ExerciseDef("Jump Rope", MuscleGroup.FULL_BODY, ExerciseType.CARDIO, listOf("skipping", "speed rope"), 0f, 3, 100),

        // Flexibility
        ExerciseDef("Yoga Flow", MuscleGroup.FULL_BODY, ExerciseType.FLEXIBILITY, listOf("vinyasa", "hatha yoga", "sun salutation"), 0f, 1, 30),
        ExerciseDef("Static Stretching", MuscleGroup.FULL_BODY, ExerciseType.FLEXIBILITY, listOf("cooldown stretch", "post workout stretch"), 0f, 1, 15),
        ExerciseDef("Dynamic Stretching", MuscleGroup.FULL_BODY, ExerciseType.FLEXIBILITY, listOf("mobility warmup", "warmup stretch"), 0f, 1, 10),
        ExerciseDef("Foam Rolling", MuscleGroup.FULL_BODY, ExerciseType.FLEXIBILITY, listOf("myofascial release", "roller"), 0f, 1, 15)
    )

    fun search(query: String): List<ExerciseDef> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return allExercises
        return allExercises.filter { def ->
            def.name.lowercase().contains(q) ||
            def.aliases.any { it.contains(q) } ||
            def.muscleGroup.displayName.lowercase().contains(q) ||
            def.exerciseType.displayName.lowercase().contains(q)
        }
    }

    fun findByName(name: String): ExerciseDef? {
        val clean = name.trim().lowercase()
        return allExercises.firstOrNull { it.name.lowercase() == clean || it.aliases.any { a -> a == clean } }
    }
}
