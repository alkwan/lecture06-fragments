package edu.uw.fragmentdemo

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class MainActivity : AppCompatActivity(), MovieListFragment.OnMovieSelectedListener {

    private val TAG = "MainActivity"

    private lateinit var adapter: ArrayAdapter<Movie>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //respond to search button clicking
    fun handleSearchClick(v: View) {
        val searchEdit = findViewById<EditText>(R.id.txt_search)
        val searchTerm = searchEdit.text.toString()

        // create a fragment
        val fragment = MovieListFragment.newInstance(searchTerm)

        // show it on the screen
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "MOVIE_LIST_FRAGMENT")
        // have remove, replace... etc.
        // to execute it and make it occur, you have to commit the transaction
        ft.commit()
    }

    override fun onMovieSelected(movie: Movie) {
        val details = DetailFragment.newInstance(movie.title, movie.description)
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, details, "MOVIE_DETAILS_FRAGMENT")
                .addToBackStack(null)
                .commit()

    }
}
