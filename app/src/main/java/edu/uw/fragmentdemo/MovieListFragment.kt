package edu.uw.fragmentdemo


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * A simple [Fragment] subclass.
 *
 */
class MovieListFragment : Fragment() {

    private lateinit var adapter: ArrayAdapter<Movie>
    private val TAG = "ListFragment"
    interface OnMovieSelectedListener {
        fun onMovieSelected(movie: Movie) {

        }
    }

    companion object {
        val SEARCH_TERM_KEY = "search term"

        // take the parameters you care about and put them in a bundle
        fun newInstance(searchTerm: String) : MovieListFragment {
            val args = Bundle().apply {
                putString(SEARCH_TERM_KEY, searchTerm)
//                putInt("year key", year)
//                putString("actor key", actor)
            }

            val theFragment = MovieListFragment()
            theFragment.arguments = args

            return theFragment
        }
    }

    private var callback:OnMovieSelectedListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = context as? OnMovieSelectedListener // try to cast
        if(callback == null) {
            throw ClassCastException("$context must implement OnMovieSelectedListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_movie_list, container, false)

        /** Fragments are designed to be self contained
         * Not supposed to know or care about what activity they're inside
         * Can't get reference to activity and findViewById on it
         * Fragment should never care which activity it's associated with
         * Fragment needs to be modular and self-contained! Only time you should access an activity
         * is when you're trying to find a context!
         */
        adapter = ArrayAdapter(activity, R.layout.list_item, R.id.txt_item, ArrayList())
        val listView = rootView.findViewById<ListView>(R.id.list_view)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val movie = parent.getItemAtPosition(position) as Movie
            Log.v(TAG, "You clicked on: $movie")

            // show the detail fragment
            callback!!.onMovieSelected(movie)
        }

        val searchTerm = arguments?.let {
            val searchTerm = it.getString(SEARCH_TERM_KEY)
            if (searchTerm != null) {
                downloadMovieData(searchTerm)
            }
        }

        return rootView
    }

    //download media information from iTunes
    private fun downloadMovieData(searchTerm: String) {
        var urlString = ""
        try {
            urlString = "https://itunes.apple.com/search?term=" + URLEncoder.encode(searchTerm, "UTF-8") + "&media=movie&entity=movie&limit=25"
            //Log.v(TAG, urlString);
        } catch (uee: UnsupportedEncodingException) {
            Log.e(TAG, uee.toString())
            return
        }

        val request = JsonObjectRequest(Request.Method.GET, urlString, null,
                Response.Listener { response ->
                    val movies = ArrayList<Movie>()

                    try {
                        //parse the JSON results
                        val results = response.getJSONArray("results") //get array from "search" key
                        for (i in 0 until results.length()) {
                            val track = results.getJSONObject(i)
                            if (track.getString("wrapperType") != "track")
                            //skip non-track results
                                continue
                            val title = track.getString("trackName")
                            val year = track.getString("releaseDate")
                            val description = track.getString("longDescription")
                            val url = track.getString("trackViewUrl")
                            val movie = Movie(title, year, description, url)
                            movies.add(movie)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    adapter.clear()
                    for (movie in movies) {
                        adapter.add(movie)
                    }
                }, Response.ErrorListener { error -> Log.e(TAG, error.toString()) })

        VolleyService.getInstance(activity!!).add(request)
    }


}
