package com.example.quizapp

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quiz_question.*
import kotlinx.coroutines.runBlocking

class QuizQuestionActivity : AppCompatActivity(), View.OnClickListener {

    private var mCurrentPosition: Int = 1
    private var mQuestionsList: ArrayList<Question>? = null
    private var numberOfQuestions: Int = 0
    private var mSelectedOption: Int = 0
    private var mCorrectAnswers: Int = 0
    private var mUserName: String? = null
    private var hasBinChosen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)


        mUserName = intent.getStringExtra(Constants.USER_NAME)

        mQuestionsList = getApiQuestions()

        setQuestion()

        tv_option_one.setOnClickListener(this)
        tv_option_two.setOnClickListener(this)
        tv_option_three.setOnClickListener(this)
        tv_option_four.setOnClickListener(this)
        btn_submit.setOnClickListener(this)

    }

    private fun answerView(answer: Int, drawableView: Int) {
        when(answer) {
            1 ->{
                tv_option_one.background = ContextCompat.getDrawable(this, drawableView)
            }

            2 ->{
                tv_option_two.background = ContextCompat.getDrawable(this, drawableView)
            }

            3 ->{
                tv_option_three.background = ContextCompat.getDrawable(this, drawableView)
            }

            4 ->{
                tv_option_four.background = ContextCompat.getDrawable(this, drawableView)
            }
        }
    }

    private fun setQuestion() {

        val question = mQuestionsList!![mCurrentPosition-1]

        defaultOptionsView()

        if(mCurrentPosition == mQuestionsList!!.size) {
            btn_submit.text = "FERDIG"
        }else {
            btn_submit.text = "SUBMIT"
        }

        progressBar.progress = mCurrentPosition
        tv_progress.text = "${mCurrentPosition}" + "/" + numberOfQuestions
        tv_question.text = question!!.question
        /*iv_image.setImageResource(question.image)*/
        tv_option_one.text = question.OptionOne
        tv_option_two.text = question.OptionTwo
        tv_option_three.text = question.OptionThree
        tv_option_four.text = question.OptionFour
    }

    private fun defaultOptionsView() {
        val options = ArrayList<TextView>()
        options.add(0, tv_option_one)
        options.add(1, tv_option_two)
        options.add(2, tv_option_three)
        options.add(3, tv_option_four)

        for (option in options) {
            option.setTextColor(android.graphics.Color.parseColor("#ff669900"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this, R.drawable.default_option_border_bg)
        }
    }

    override fun onClick(v: View?) {

        when(v?.id) {

            R.id.tv_option_one -> {
                selectedOptionView(tv_option_one, 1)
                hasBinChosen = true
            }
            R.id.tv_option_two -> {
                selectedOptionView(tv_option_two, 2)
                hasBinChosen = true
            }
            R.id.tv_option_three -> {
                selectedOptionView(tv_option_three, 3)
                hasBinChosen = true
            }
            R.id.tv_option_four -> {
                selectedOptionView(tv_option_four, 4)
                hasBinChosen = true
            }

            R.id.btn_submit -> {
                if(mCurrentPosition >= mQuestionsList!!.size && hasBinChosen == true) {
                    val question = mQuestionsList?.get(mCurrentPosition -1)

                    if(question!!.CorrectAnswer != mSelectedOption) {
                        answerView(mSelectedOption, R.drawable.wrong_option_border_bg)
                        answerView(question.CorrectAnswer, R.drawable.correct_option_border_bg)


                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this, ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME, mUserName)
                            intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionsList!!.size)
                            startActivity(intent)
                            finish()
                        }, 1000)
                        hasBinChosen = false
                    }else{
                        answerView(question.CorrectAnswer, R.drawable.correct_option_border_bg)
                        mCorrectAnswers++

                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this, ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME, mUserName)
                            intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionsList!!.size)
                            startActivity(intent)
                            finish()
                        }, 1000)
                        hasBinChosen = false
                    }
                    mCurrentPosition++

                } else if(hasBinChosen == true){
                    val question = mQuestionsList?.get(mCurrentPosition -1 )

                    if(question!!.CorrectAnswer != mSelectedOption) {
                        answerView(mSelectedOption, R.drawable.wrong_option_border_bg)
                        answerView(question.CorrectAnswer, R.drawable.correct_option_border_bg)

                        Handler(Looper.getMainLooper()).postDelayed({
                            setQuestion()
                        }, 1000)
                        hasBinChosen = false


                    }else{
                        answerView(question.CorrectAnswer, R.drawable.correct_option_border_bg)
                        mCorrectAnswers++

                        Handler(Looper.getMainLooper()).postDelayed({
                            setQuestion()
                        }, 1000)
                        hasBinChosen = false
                    }
                    mCurrentPosition++
                }else {
                    Toast.makeText(this, "Velg et alternativ!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
        defaultOptionsView()
        mSelectedOption = selectedOptionNum

        tv.setTextColor(android.graphics.Color.parseColor("#ffaa66cc"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
    }

    fun getApiQuestions(): ArrayList<Question> {

        val gson = Gson()
        val theList = ArrayList<Question>()

        runBlocking {
            try {
                val response = Fuel.get("https://opentdb.com/api.php?amount=20&category=17&type=multiple").awaitString()
                Log.d("TEST1", response)
                val questions = gson.fromJson(response, Questions::class.java)
                val theQuestions = questions.results


                for (item in theQuestions.indices) {
                    val answers = ArrayList<String>()
                    val correct_answer: Int
                    for (answer in theQuestions[item].incorrect_answers) {
                        answers.add(answer)
                    }
                    answers.add(theQuestions[item].correct_answer)
                    answers.shuffle()
                    correct_answer = answers.indexOf(theQuestions[item].correct_answer)

                    val q1 = Question(
                        item,
                        Html.fromHtml(theQuestions[item].question).toString(),
                        R.drawable.sirius,
                        Html.fromHtml(answers[0]).toString(),
                        Html.fromHtml(answers[1]).toString(),
                        Html.fromHtml(answers[2]).toString(),
                        Html.fromHtml(answers[3]).toString(),
                        correct_answer+1
                    )

                    theList.add(q1)
                }

                numberOfQuestions = theList.size
                progressBar.max = numberOfQuestions
                Log.d("TEST2", questions.results[0].question)

            }
            catch (e  : Exception) {
                Log.e("FUCK", e.message)

            }
        }

        return theList
    }

}




data class Questions(val response_code: String?, val results: Array<TheQuestion>)
data class TheQuestion(val category: String, val type: String, val difficulty: String, val question: String, val correct_answer: String, val incorrect_answers: Array<String>)