package com.easv.tkm.recipeapp.GUI

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.easv.tkm.recipeapp.DAL.RecipeRepository
import com.easv.tkm.recipeapp.R
import com.easv.tkm.recipeapp.RecyclerAdapter.RecyclerAdapter
import com.easv.tkm.recipeapp.data.IntentValues
import com.easv.tkm.recipeapp.data.Models.Category
import com.easv.tkm.recipeapp.data.Models.Recipe
import com.easv.tkm.recipeapp.data.interfaces.IClickItemListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class MainActivity : AppCompatActivity(), IClickItemListener<Recipe> {

    private var recipeRepository = RecipeRepository.get()
    private lateinit var adapter: RecyclerAdapter
    private var selectedCategory: Category? = null
    private var userTouch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerForContextMenu(recyclerView)
        btnSearch.setOnClickListener { view -> searchText() }
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecyclerAdapter(this, this)
        recyclerView.adapter = adapter

        spCategory.setOnTouchListener(View.OnTouchListener { v, event ->
            this.userTouch = true; false})

        spCategory.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                if (userTouch && position != 0) {
                    selectedCategory = spCategory.selectedItem as Category
                    userTouch = false
                    searchText()
                }
                else if(userTouch && position == 0){
                    selectedCategory = null
                    userTouch = false
                    searchText()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        this.initializeSpinner()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.create_recipe -> { openCreateActivity(); true }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openCreateActivity(){
        val intent = Intent(this, CrudActivity::class.java)
        startActivityForResult(intent, IntentValues.REQUEST_DETAIL.code)
    }

    fun searchText(){
        val text: String = searchBar.text.toString()
        adapter.filter(text, selectedCategory)
    }

    override fun onItemClick(recipe: Recipe) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("RECIPE", recipe)
        startActivityForResult(intent, IntentValues.REQUEST_DETAIL.code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var job: Deferred<Unit>? = null

        if(requestCode == IntentValues.REQUEST_DETAIL.code && resultCode == IntentValues.RESPONSE_DETAIL_CREATE.code) {
            job = GlobalScope.async {}
        }

        else if(requestCode == IntentValues.REQUEST_DETAIL.code && resultCode == IntentValues.RESPONSE_DETAIL_UPDATE.code) {
            job = GlobalScope.async {}
        }

        else if(requestCode == IntentValues.REQUEST_DETAIL.code && resultCode == IntentValues.RESPONSE_DETAIL_DELETE.code) {
            job = GlobalScope.async {}
        }

        job?.let {
            job.invokeOnCompletion { _ -> searchText() }
        }
    }

    fun initializeSpinner(){
        val categories = ArrayList<Category>()
        categories.apply {
            add(Category(0, "All"))
            add(Category(1, "Morgenmad"))
            add(Category(2, "Aftensmad"))
            add(Category(3, "Dessert"))
        }

        spCategory.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
    }
}