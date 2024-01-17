import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import com.example.ifmapp.R

class CustomDialogClass(private val activity: Activity) : Dialog(activity),
    View.OnClickListener {

    private lateinit var cross: ImageView
    private lateinit var done: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.leave_approve_dialog)

        cross = findViewById(R.id.cross_leaveDialog)
        done = findViewById(R.id.done_btn)

        cross.setOnClickListener(this)
        done.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cross_leaveDialog -> activity.finish()
            R.id.done_btn -> dismiss()
            else -> {
            }
        }
        dismiss()
    }


}
