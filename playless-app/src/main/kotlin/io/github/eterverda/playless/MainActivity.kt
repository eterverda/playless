package io.github.eterverda.playless

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

public class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_settings ->
                return true
        }

        return super.onOptionsItemSelected(item)
    }
}
