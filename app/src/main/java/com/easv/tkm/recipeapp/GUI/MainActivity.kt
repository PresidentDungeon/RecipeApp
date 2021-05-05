package com.easv.tkm.recipeapp.GUI

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.easv.tkm.recipeapp.DAL.RecipeRepository
import com.easv.tkm.recipeapp.R
import com.easv.tkm.recipeapp.RecyclerAdapter.RecyclerAdapter
import com.easv.tkm.recipeapp.data.IntentValues
import com.easv.tkm.recipeapp.data.Models.Category
import com.easv.tkm.recipeapp.data.Models.RecipeWithIngredients
import com.easv.tkm.recipeapp.data.Sorting
import com.easv.tkm.recipeapp.data.interfaces.IClickItemListener
import com.easv.tkm.recipeapp.data.interfaces.IMenuUpdate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class MainActivity : AppCompatActivity(), IClickItemListener<RecipeWithIngredients>, IMenuUpdate {

    private var recipeRepository = RecipeRepository.get()
    private lateinit var adapter: RecyclerAdapter
    private var selectedCategory: Category? = null
    private var userTouch = false
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerForContextMenu(recyclerView)
        btnSearch.setOnClickListener { view -> searchText() }
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecyclerAdapter(this, this, this)
        recyclerView.adapter = adapter
        val manager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = manager

        spCategory.setOnTouchListener(View.OnTouchListener { v, event -> this.userTouch = true; false })

        spCategory.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                if (userTouch && position != 0) {
                    selectedCategory = spCategory.selectedItem as Category
                    userTouch = false
                    searchText()
                } else if (userTouch && position == 0) {
                    selectedCategory = null
                    userTouch = false
                    searchText()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        recipeRepository.getCategories().observe(this, Observer { categories -> val categoryList = categories.toMutableList(); categoryList.add(0, Category(0, "All")); spCategory.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryList) })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.create_recipe -> { openCreateActivity(); true }
            R.id.sortName -> {this.adapter.setSortingType(Sorting.SORTING_NAME); searchText(); true}
            R.id.sortAge -> {this.adapter.setSortingType(Sorting.SORTING_AGE); searchText(); true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun updateMenu(sortingType: Sorting) {
        var nameMenuItem = menu.findItem(R.id.sortName)
        var ageMenuItem = menu.findItem(R.id.sortAge)

        when(sortingType){
            Sorting.SORTING_NAME -> {nameMenuItem.title = "Name (desc)"; ageMenuItem.title = "Age (asc)"}
            Sorting.SORTING_NAME_DESC -> {nameMenuItem.title = "Name (asc)"; ageMenuItem.title ="Age (asc)"}
            Sorting.SORTING_AGE -> {nameMenuItem.title = "Name (asc)"; ageMenuItem.title = "Age (desc)"}
            Sorting.SORTING_AGE_DESC -> {nameMenuItem.title = "Name (asc)"; ageMenuItem.title = "Age (asc)"}
        }
    }

    fun openCreateActivity(){
        val intent = Intent(this, CrudActivity::class.java)
        startActivityForResult(intent, IntentValues.REQUESTCODE_CRUD.code)
    }

    fun searchText(){
        val text: String = searchBar.text.toString()
        adapter.filter(text, selectedCategory)
    }

    override fun onItemClick(recipe: RecipeWithIngredients) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("RECIPE", recipe.recipe)
        intent.putExtra("INGREDIENTS", recipe.ingredientEntries.toTypedArray())
        startActivityForResult(intent, IntentValues.REQUEST_DETAIL.code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var job: Deferred<Unit>? = null

        if(requestCode == IntentValues.REQUESTCODE_CRUD.code && resultCode == IntentValues.RESPONSE_DETAIL_CREATE.code) {
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
}