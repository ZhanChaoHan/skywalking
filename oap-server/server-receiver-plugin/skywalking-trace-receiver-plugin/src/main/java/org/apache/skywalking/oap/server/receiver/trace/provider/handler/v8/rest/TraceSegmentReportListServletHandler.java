/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.oap.server.receiver.trace.provider.handler.v8.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.skywalking.apm.network.language.agent.v3.SegmentObject;
import org.apache.skywalking.oap.server.library.module.ModuleManager;
import org.apache.skywalking.oap.server.library.util.ProtoBufJsonUtils;

/**
 * Parsing segments from list json
 */
public class TraceSegmentReportListServletHandler extends TraceSegmentReportBaseServletHandler {

    private final Gson gson = new Gson();

    public TraceSegmentReportListServletHandler(ModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    protected List<SegmentObject> parseSegments(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        final JsonArray array = gson.fromJson(stringBuilder.toString(), JsonArray.class);
        if (array.size() == 0) {
            return Collections.emptyList();
        }

        final ArrayList<SegmentObject> segments = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            SegmentObject.Builder upstreamSegmentBuilder = SegmentObject.newBuilder();
            ProtoBufJsonUtils.fromJSON(element.toString(), upstreamSegmentBuilder);
            segments.add(upstreamSegmentBuilder.build());
        }
        return segments;
    }

    @Override
    public String pathSpec() {
        return "/v3/segments";
    }
}
