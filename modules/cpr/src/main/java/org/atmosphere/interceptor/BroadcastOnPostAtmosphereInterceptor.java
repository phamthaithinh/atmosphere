/*
 * Copyright 2014 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.interceptor;

import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereInterceptorAdapter;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.atmosphere.util.IOUtils.readEntirelyAsByte;
import static org.atmosphere.util.IOUtils.readEntirelyAsString;

/**
 * This read the request's body and invoke the associated {@link org.atmosphere.cpr.Broadcaster} of an {@link AtmosphereResource}.
 * The broadcast always happens AFTER the request has been delivered to an {@link org.atmosphere.cpr.AtmosphereHandler}.
 *
 * @author Jeanfrancois Arcand
 */
public class BroadcastOnPostAtmosphereInterceptor extends AtmosphereInterceptorAdapter {

    private final static Logger logger = LoggerFactory.getLogger(BroadcastOnPostAtmosphereInterceptor.class);

    @Override
    public Action inspect(AtmosphereResource r) {
        return Action.CONTINUE;
    }


    @Override
    public void postInspect(AtmosphereResource r) {
        if (r.getRequest().getMethod().equalsIgnoreCase("POST")) {
            AtmosphereRequest request = r.getRequest();
            AtmosphereRequest.Body body = request.body();
            if (body.hasString()) {
                StringBuilder b = readEntirelyAsString(r);
                if (b.length() > 0) {
                    r.getBroadcaster().broadcast(b.toString());
                } else {
                    logger.warn("{} received an empty body", BroadcastOnPostAtmosphereInterceptor.class.getSimpleName());
                }
            } else {
                byte[] b = readEntirelyAsByte(r);
                if (b.length > 0) {
                    r.getBroadcaster().broadcast(b);
                } else {
                    logger.warn("{} received an empty body", BroadcastOnPostAtmosphereInterceptor.class.getSimpleName());
                }
            }
        }
    }
}
