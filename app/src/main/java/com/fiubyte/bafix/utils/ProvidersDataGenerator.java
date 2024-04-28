package com.fiubyte.bafix.utils;

import com.fiubyte.bafix.entities.ProviderData;
import com.fiubyte.bafix.entities.ServiceData;

import java.util.ArrayList;
import java.util.Arrays;

public class ProvidersDataGenerator {
    public static ArrayList<ProviderData> generateProvidersList(ArrayList<ServiceData> services) {
        ArrayList<ProviderData> providers = new ArrayList<>();
        for(ServiceData service : services) {
            ProviderData provider = providers.stream().filter(p -> p.getId() == service.getProviderId())
                    .findFirst()
                    .orElse(null);
            if(provider != null) {
                provider.addService(service);
            } else {
                ProviderData newProvider = new ProviderData(
                        service.getProviderId(),
                        service.getProviderName(),
                        service.getGeoPoint(),
                        new ArrayList(Arrays.asList(service)),
                        service.getMaxDistance(),
                        service.getProviderPhotoURL(),
                        service.getProviderPhone()
                );
                providers.add(newProvider);
            }
        }
        return providers;
    }
}
