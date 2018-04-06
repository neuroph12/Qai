/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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
 * Created by zenpunk on 12/27/15.
 */
public interface ProcedureConstants {

    enum ProcedureState {TEMPLATE, READY, STARTED, ENDED, INTERRUPTED, ERROR}

    String INPUT_TIME_SEQUENCE = "time-sequence";

    String INPUT_NETWORK = "network";

    String INPUT_NEURAL_NETWORK = "neural-network";

    String INPUT_MATRIX = "matrix";

    String MATRIX_METRICS = "matrix metrics";

    String MATRIX_DATA_METRICS = "matrix data metrics";

    String NETWORK_METRICS = "network metrics";

    String TIME_SEQUENCE_METRICS = "time-sequence metrics";

    String INPUT_STOCK_ENTITY_COLLECTION = "input stock entity collection";

    String TRAINED_NEURAL_NETWORK = "trained neural network";

    String FROM = "from";

    String CRITERIA = "criteria";

    String AVERAGE_TIME_SEQUENCE = "average time-sequence";

    String CHANGE_POINTS = "change points";

    String MAP_OF_TIME_SEQUENCE = "time-sequence map";

    String SORTED_ITEMS = "sorted items";

    String INPUT_NAMES = "input names";

    String PROCEDURES = "PROCEDURES";

    String PROCESS_ENDED = "PROCESS_ENDED";

    String PROCESS_INTERRUPTED = "PROCESS_INTERRUPTED";

    String PROCESS_ERROR = "PROCESS_ERROR";

    String PARAMETER_NAME = "parameter name";

    String POINTER_OR_DATA_VALUE = "pointer to or data of value";

    String USER_NAME = "USER_NAME";

    String PASSWORD = "PASSWORD";

    String USER_UUID = "USER_UUID";

    String STOCK_ENTITY = "STOCK_ENTITY";

    String NUMBER_OF_INSERTS = "numberOfInserts";

    String TARGET_COLLECTION = "TARGET_COLLECTION";

    String PROCEDURE_TEMPLATE = "PROCEDURE_TEMPLATE";

    String TARGET_INPUT_NAME = "TARGET_INPUT_NAME";

    String MIMETYPE_STRING = "MIMETYPE_STRING";

    String MIMETYPE_NUMBER = "MIMETYPE_NUMBER";

    String MIMETYPE_BOOLEAN = "MIMETYPE_BOOLEAN";

    String MIMETYPE_TIME_SERIES = "MIMETYPE_TIME_SERIES";

    String MIMETYPE_CHANGE_POINT_MARKER = "MIMETYPE_CHANGE_POINT_MARKER";

    String MIMETYPE_MATRIX = "MIMETYPE_MATRIX";

    String MIMETYPE_METRICS = "MIMETYPE_METRICS";

    String MIMETYPE_SEARCH_RESULT = "MIMETYPE_SEARCH_RESULT";

    String MIMETYPE_NEURAL_NETWORK = "MIMETYPE_NEURAL_NETWORK";

    String MIMETYPE_NETWORK = "MIMETYPE_NETWORK";

    String MIMETYPE_VECTOR = "MIMETYPE_VECTOR";

    String MIMETYPE_STRING_LIST = "MIMETYPE_STRING_LIST";

    String MIMETYPE_DATE_LIST = "MIMETYPE_DATE_LIST";

    String MIMETYPE_STOCK_ENITIY_LIST = "MIMETYPE_STOCK_ENITIY_LIST";

    String MIMETYPE_TIME_SEQUENCE_MAP = "MIMETYPE_TIME_SEQUENCE_MAP";

    String MIMETYPE_PROCEDURE = "MIMETYPE_PROCEDURE";
}
