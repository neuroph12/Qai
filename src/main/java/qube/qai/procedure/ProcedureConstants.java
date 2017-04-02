/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.procedure;

/**
 * Created by rainbird on 12/27/15.
 */
public interface ProcedureConstants {

    public static String INPUT_TIME_SEQUENCE = "time-sequence";

    public static String INPUT_NETWORK = "network";

    public static String INPUT_NEURAL_NETWORK = "neural-network";

    public static String INPUT_MATRIX = "matrix";

    public static String MATRIX_METRICS = "matrix metrics";

    public static String MATRIX_DATA_METRICS = "matrix data metrics";

    public static String NETWORK_METRICS = "network metrics";

    public static String TIME_SEQUENCE_METRICS = "time-sequence metrics";

    public static String FROM = "from";

    public static String CRITERIA = "criteria";

    public static String AVERAGE_TIME_SEQUENCE = "average time-sequence";

    public static String CHANGE_POINTS = "change points";

    public static String MAP_OF_TIME_SEQUENCE = "time-sequence map";

    public static String SORTED_ITEMS = "sorted items";

    public static String INPUT_NAMES = "input names";

    public static final String PROCEDURES = "PROCEDURES";

    public final static String PROCESS_ENDED = "PROCESS_ENDED";

    public final static String PROCESS_INTERRUPTED = "PROCESS_INTERRUPTED";

    public final static String PROCESS_ERROR = "PROCESS_ERROR";
}
