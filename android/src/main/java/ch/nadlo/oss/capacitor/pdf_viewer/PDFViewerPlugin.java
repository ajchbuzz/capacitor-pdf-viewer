package ch.nadlo.oss.capacitor.pdf_viewer;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "PDFViewer")
public class PDFViewerPlugin extends Plugin {
    private PdfViewerFragment pdfFragment;

    @PluginMethod
    public void open(PluginCall call) {
        String url = call.getString("url");
        Integer top = call.getInt("top", 0);

        if (url != null && !url.isEmpty()) {
            // Obtener la actividad actual
            Activity activity = getActivity();

            if (!(activity instanceof FragmentActivity)) {
                call.reject("Activity is not a FragmentActivity");
                return;
            }

            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();

            // Crear una nueva instancia del fragmento y mostrarlo
            fragmentActivity.runOnUiThread(() -> {
                pdfFragment = PdfViewerFragment.newInstance(url, top);
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, pdfFragment)  // Usa el contenedor predeterminado
                        .addToBackStack(null)  // Permite volver atrás
                        .commit();

                call.resolve();
            });
        }
    }

    @PluginMethod
    public void close(PluginCall call) {
        // Obtener la actividad actual
        Activity activity = getActivity();
        if (!(activity instanceof FragmentActivity)) {
            call.reject("Activity is not a FragmentActivity");
            return;
        }

        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();

        // Ejecutar en el hilo de la UI para cerrar el fragmento
        fragmentActivity.runOnUiThread(() -> {
            if (pdfFragment != null) {
                fragmentManager.beginTransaction()
                        .remove(pdfFragment)
                        .commit();
                pdfFragment = null; // Limpiar la referencia del fragmento
                call.resolve();
            } else {
                call.reject("No PDF viewer is currently open");
            }
        });
    }
}
