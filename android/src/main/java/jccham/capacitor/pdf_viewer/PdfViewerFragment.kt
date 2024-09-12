package jccham.capacitor.pdf_viewer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.HeaderData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class PdfViewerFragment : Fragment() {

    private lateinit var pdfUrl: String
    private var top: Int = 0
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    companion object {
        fun newInstance(pdfUrl: String, top: Int): PdfViewerFragment {
            val fragment = PdfViewerFragment()
            val args = Bundle()
            args.putString("pdfUrl", pdfUrl)
            args.putInt("top", top)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            pdfUrl = it.getString("pdfUrl").orEmpty()
            top = it.getInt("top", 0)
        }

        // Crear el FrameLayout principal
        val rootLayout = FrameLayout(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Crear el PdfRendererView para mostrar el PDF
        val pdfRendererView = PdfRendererView(requireContext()).apply {
            rootLayout.addView(this)
        }

        // Inicializar la vista PDF con la URL usando el método initWithUrl
        pdfRendererView.initWithUrl(
            pdfUrl,
            HeaderData(), // Puedes personalizar los headers aquí si es necesario
            lifecycleCoroutineScope = lifecycleScope, // Pasamos el LifecycleCoroutineScope correcto
            lifecycle = lifecycle // Pasamos el ciclo de vida del fragmento
        )

        // Aplicar el margen superior dinámico al layout completo
        val params = rootLayout.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = top
        rootLayout.layoutParams = params

        return rootLayout
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancelar el Job cuando el fragmento se destruya
    }
}
