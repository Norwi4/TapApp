package com.norwi.tapapp2


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ProductsAdapter
    private val products = mutableListOf<Product>()

    private val prefs by lazy {
        getSharedPreferences("products_prefs", Context.MODE_PRIVATE)
    }

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadProducts()

        val recycler = findViewById<RecyclerView>(R.id.recyclerProducts)
        val editText = findViewById<EditText>(R.id.editTextProduct)
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)

        adapter = ProductsAdapter(products) { product, isBought ->
            product.isBought = isBought
            saveProducts()
        }
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        // ✅ Прятать клавиатуру при прокрутке списка
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    hideKeyboard()
                }
            }
        })

        buttonAdd.setOnClickListener {
            val name = editText.text.toString().trim()
            if (name.isNotEmpty()) {
                val product = Product(System.currentTimeMillis(), name)
                products.add(product)
                adapter.notifyItemInserted(products.size - 1)
                editText.text.clear()
                saveProducts()
            }
        }

        editText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_NULL ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                buttonAdd.performClick()
                true
            } else {
                false
            }
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                products.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                saveProducts()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val paint = Paint()
                    val red = Color.parseColor("#FF5252")

                    val alpha = (255 * (dX / itemView.width).coerceIn(0f, 1f)).toInt()
                    paint.color = Color.argb(alpha, Color.red(red), Color.green(red), Color.blue(red))

                    c.drawRect(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        itemView.left + dX,
                        itemView.bottom.toFloat(),
                        paint
                    )
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        itemTouchHelper.attachToRecyclerView(recycler)
    }

    private fun saveProducts() {
        val json = gson.toJson(products)
        prefs.edit().putString("products", json).apply()
    }

    private fun loadProducts() {
        val json = prefs.getString("products", null)
        if (json != null) {
            val type = object : TypeToken<List<Product>>() {}.type
            val loaded = gson.fromJson<List<Product>>(json, type)
            products.clear()
            products.addAll(loaded)
        }
    }

    // 👇 Метод для скрытия клавиатуры
    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}