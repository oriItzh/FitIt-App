import android.app.Application
import com.example.architectureproject.data.local_db.ItemDatabase
import com.example.archtectureproject.data.local.ExerciseDao
import com.example.archtectureproject.data.model.Exercise
import com.example.archtectureproject.data.model.PersonalData

class ExerciseRepository(application: Application) {

    private var exerciseDao: ExerciseDao?

    init {
        val db  = ItemDatabase.getDatabase(application)
        exerciseDao = db.exerciseDao()
    }

    fun getAllExercises() = exerciseDao?.getItems()

    suspend fun insert(exercise: Exercise) {
        exerciseDao?.insertItem(exercise)
    }

    suspend fun update(exercise: Exercise) {
        exerciseDao?.updateItem(exercise)
    }

    suspend fun delete(exercise: Exercise) {
        exerciseDao?.deleteItem(exercise)
    }

    suspend fun deleteAll() {
        exerciseDao?.deleteAll()
    }

//    private val db = Room.databaseBuilder(
//        context.applicationContext,
//        ExerciseDatabase::class.java, "exercise-database"
//    ).build()
//
//    private val exerciseDao = db.exerciseDao()
//
//    fun getAllExercises(): LiveData<List<Exercise>> = exerciseDao.getAllExercises()
//
//    fun insert(exercise: Exercise) {
////        withContext(Dispatchers.IO) {
////        }
//            exerciseDao.insert(exercise)
//    }
//
//    fun update(exercise: Exercise) {
////        withContext(Dispatchers.IO) {
////        }
//            exerciseDao.update(exercise)
//    }
//
//    fun delete(exercise: Exercise) {
////        withContext(Dispatchers.IO) {
////        }
//            exerciseDao.delete(exercise)
//    }

}
