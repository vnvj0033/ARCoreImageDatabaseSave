package com.vnvj0033.arcoredatabasesave

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Session
import org.apache.commons.lang3.RandomStringUtils
import java.io.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var loadButton: Button
    private lateinit var addButton: Button
    private lateinit var saveButton: Button
    private lateinit var resetButton: Button
    private lateinit var databaseSizeView: TextView
    private lateinit var databaseListView: TextView
    private lateinit var arDatabase: AugmentedImageDatabase

    private var session: Session? = null

    private var nameList = ArrayList<String>()

    private lateinit var databaseFile: File
    private lateinit var nameListFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
            return
        }

        if (session == null)
            init()
    }

    private fun clickAdd() {
        val imageInputStream = assets.open("image.png")
        val bitmap = BitmapFactory.decodeStream(imageInputStream)
        imageInputStream.close()

        val name = getRadomString(10)

        arDatabase.addImage(name, bitmap)
        nameList.add(name)

        displayText()
    }

    private fun clickSave() {
        val dataBaseFOS = FileOutputStream(databaseFile)
        arDatabase.serialize(dataBaseFOS)

        val nameListFOS = FileOutputStream(nameListFile)
        val oos = ObjectOutputStream(nameListFOS)
        oos.writeObject(nameList)

        dataBaseFOS.close()
        oos.close()
        nameListFOS.close()
    }

    private fun clickLoad() {
        if(!databaseFile.exists()) return

        val fis = FileInputStream(databaseFile)
        arDatabase = AugmentedImageDatabase.deserialize(session, fis)

        val nameListFIS = FileInputStream(nameListFile)
        val ois = ObjectInputStream(nameListFIS)
        nameList = ois.readObject() as ArrayList<String>

        nameListFIS.close()
        ois.close()
        fis.close()
        displayText()
    }

    private fun clickReset() {
        arDatabase = AugmentedImageDatabase(session)
        nameList = ArrayList()

        displayText()
    }

    private fun init() {
        initView()
        initButtonClickListener()
        session = Session(this)
        arDatabase = AugmentedImageDatabase(session)
        databaseFile = File(filesDir, "/db.imdb")
        nameListFile = File(filesDir, "/nameList.array")
        displayText()
    }

    private fun getRadomString(size: Int): String {
        return RandomStringUtils.randomAlphanumeric(size)
    }

    private fun initView() {
        addButton = findViewById(R.id.addButton)
        saveButton = findViewById(R.id.saveButton)
        loadButton = findViewById(R.id.loadButton)
        resetButton = findViewById(R.id.resetButton)
        databaseSizeView = findViewById(R.id.databaseSizeView)
        databaseListView = findViewById(R.id.databaseListView)


    }

    private fun initButtonClickListener() {
        addButton.setOnClickListener {
            clickAdd()
        }

        saveButton.setOnClickListener {
            clickSave()
        }

        loadButton.setOnClickListener {
            clickLoad()
        }

        resetButton.setOnClickListener {
            clickReset()
        }
    }

    private fun displayText() {
        databaseSizeView.text = arDatabase.numImages.toString()
        databaseListView.text = nameList.toString()
    }
}