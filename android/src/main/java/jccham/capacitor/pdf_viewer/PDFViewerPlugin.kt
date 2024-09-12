package jccham.capacitor.pdf_viewer

import androidx.fragment.app.FragmentActivity
import com.getcapacitor.Plugin
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.PluginCall
import androidx.fragment.app.FragmentManager
import com.getcapacitor.PluginMethod

@CapacitorPlugin(name = "PdfViewerPlugin")
class PdfViewerPlugin : Plugin() {
    private var pdfFragment: PdfViewerFragment? = null

    @PluginMethod
    fun loadPdf(call: PluginCall) {
        val pdfUrl = call.getString("url") ?: return call.reject("URL is required")
        val top = call.getInt("top", 0) ?: return call.reject("URL is required")

        // Obtener la actividad actual
        val activity = activity
        if (activity !is FragmentActivity) {
            return call.reject("Activity is not a FragmentActivity")
        }

        val fragmentActivity = activity as FragmentActivity
        val fragmentManager: FragmentManager = fragmentActivity.supportFragmentManager

        // Crear una nueva instancia del fragmento y mostrarlo
        fragmentActivity.runOnUiThread {
            pdfFragment = PdfViewerFragment.newInstance(pdfUrl, top)
            fragmentManager.beginTransaction()
                .replace(android.R.id.content, pdfFragment!!)
                .addToBackStack(null)
                .commit()

            call.resolve()
        }
    }

    @PluginMethod
    fun close(call: PluginCall) {
        val activity = activity
        if (activity !is FragmentActivity) {
            return call.reject("Activity is not a FragmentActivity")
        }

        val fragmentActivity = activity as FragmentActivity
        val fragmentManager: FragmentManager = fragmentActivity.supportFragmentManager

        // Ejecutar en el hilo de la UI para cerrar el fragmento
        fragmentActivity.runOnUiThread {
            if (pdfFragment != null) {
                fragmentManager.beginTransaction()
                    .remove(pdfFragment!!)
                    .commit()
                pdfFragment = null
                call.resolve()
            } else {
                call.reject("No PDF viewer is currently open")
            }
        }
    }
}
