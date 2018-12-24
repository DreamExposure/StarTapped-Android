package org.dreamexposure.startapped.async;

import org.dreamexposure.startapped.objects.network.NetworkCallStatus;

/**
 * @author NovaFox161
 * Date Created: 12/24/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public interface TaskCallback {

    void taskCallback(NetworkCallStatus status);
}
