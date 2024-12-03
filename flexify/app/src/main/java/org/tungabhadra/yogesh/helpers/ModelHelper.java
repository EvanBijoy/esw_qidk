package org.tungabhadra.yogesh.helpers;


import android.app.Application;
import android.content.Context;
import com.qualcomm.qti.snpe.NeuralNetwork;
import com.qualcomm.qti.snpe.SNPE;
import java.io.InputStream;

public class ModelHelper {

    public NeuralNetwork loadModel(Context context) throws Exception {
        Application application = (Application) context.getApplicationContext();
        try (InputStream is = application.getAssets().open("model.dlc")) {
            int size = is.available();

            SNPE.NeuralNetworkBuilder builder = new SNPE.NeuralNetworkBuilder(application)
                    .setRuntimeOrder(NeuralNetwork.Runtime.DSP, NeuralNetwork.Runtime.GPU, NeuralNetwork.Runtime.CPU)
                    .setModel(is, size);

            return builder.build();
        }
    }
}
