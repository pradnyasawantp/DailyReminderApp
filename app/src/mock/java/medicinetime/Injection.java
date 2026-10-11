package medicinetime;

import android.content.Context;
import androidx.annotation.NonNull;

import medicinetime.data.source.MedicineRepository;
import medicinetime.data.source.local.MedicinesLocalDataSource;

public class Injection {

    public static MedicineRepository provideMedicineRepository(@NonNull Context context) {
        return MedicineRepository.getInstance(MedicinesLocalDataSource.getInstance(context));
    }
}
