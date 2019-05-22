package io.temco.guhada.data.viewmodel.base;

import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;

public class BaseObservableViewModel extends ViewModel implements Observable {
    private PropertyChangeRegistry registry;

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (registry == null) {
            registry = new PropertyChangeRegistry();
        }

        registry.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (registry == null) {
            registry = new PropertyChangeRegistry();
        }

        registry.remove(callback);
    }

    public void notifyChange() {
        synchronized (this) {
            if (registry == null) return;
        }
    }

    public void notifyPropertyChanged(int id) {
        synchronized (this) {
            if (registry == null) return;
        }
        registry.notifyCallbacks(this, id, null);
    }
}
