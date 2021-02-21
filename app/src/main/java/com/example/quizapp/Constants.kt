package com.example.quizapp

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import kotlinx.coroutines.runBlocking
import com.google.gson.Gson
import java.lang.Exception
import java.net.URL

object Constants {

    const val USER_NAME: String = "user_name"
    const val TOTAL_QUESTIONS: String = "total_questions"
    const val CORRECT_ANSWERS: String = "correct_answers"

    fun getQuestions(): ArrayList<Question> {
        val questionslist = ArrayList<Question>()

        val q1 = Question(
            1,
            "Hva heter Sirius til etternavn?",
            R.drawable.sirius,
            "bleek",
            "Moralis",
            "Black",
            "White",
            3
        )

        questionslist.add(q1)

        val q2 = Question(
            2,
            "Hvem gifter Harry P seg med?",
            R.drawable.sirius,
            "Noldus",
            "Gulla",
            "Voldemort",
            "Albus Dumbledore",
            2
        )

        questionslist.add(q2)

        val q3 = Question(
            3,
            "Hva ser harry i speglet \"Maerd\"?",
            R.drawable.sirius,
            "Den største osten ever",
            "Tissen sin",
            "Sirius Svart",
            "Foreldrene sine",
            4
        )

        questionslist.add(q3)

        val q4 = Question(
            4,
            "Hvilken farge har Harry Potter sine øgon?",
            R.drawable.sirius,
            "svart",
            "grønn",
            "gul",
            "blå",
            2
        )

        questionslist.add(q4)

        return questionslist
    }



    fun getApiQuestions(): ArrayList<Question> {
        //Bruk volley: https://developer.android.com/training/volley/request
        //Bruk metoden her: https://www.thecrazyprogrammer.com/2017/01/android-json-parsing-from-url-example.html

        val tag= "MainActivity" // for logging purposes
        val gson = Gson()
        val questionslist = ArrayList<Question>()

        runBlocking {
            try {
                val response = Fuel.get("https://opentdb.com/api.php?amount=2&category=17&type=multiple").awaitString()
                Log.d(tag, response)
                val questions = gson.fromJson(response, Questions::class.java)

                val questionslist = ArrayList<Question>()
                val q1 = Question(
                    1,
                    questions.results[0].question,
                    R.drawable.sirius,
                    "bleek",
                    "Moralis",
                    "Black",
                    "White",
                    3
                )

                questionslist.add(q1)

                val q2 = Question(
                    2,
                    questions.results[1].question,
                    R.drawable.sirius,
                    "Noldus",
                    "Gulla",
                    "Voldemort",
                    "Albus Dumbledore",
                    2
                )

                questionslist.add(q2)



                Log.d(tag, questions.results[0].question)

            }
            catch (e  : Exception) {
                Log.e(tag, e.message)
                //Toast.makeText(this@MainActivity, "could not find course", Toast.LENGTH_SHORT).show()
            }
        }



        return questionslist
    }
}
