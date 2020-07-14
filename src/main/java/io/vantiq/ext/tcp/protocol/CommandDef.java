package io.vantiq.ext.tcp.protocol;

import java.util.Arrays;

public class CommandDef {

    /**
     *     "command": "4C5A",
     *     "commandName": "HeartBeat",
     *     "request": {
     *       "length": 10,
     *       "fields": [
     *         {"field": "controllerNo", "length": 6, "type": "string"},
     *         {"field": "heartBeatSeq", "length": 4, "type": "integer"}
     *       ]
     *     },
     *     "response": {
     *       "length": 11,
     *       "fields": [
     *         {"field": "controllerNo", "length": 6, "type": "string"},
     *         {"field": "heartBeatSeq", "length": 4, "type": "string"},
     *         {"field": "answer", "length": 1, "type": "byte", "match": "59"}
     *       ]
     *     }
     */

    String command;
    String commandName;
    CommandRequestResponse request;
    CommandRequestResponse response;


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public CommandRequestResponse getRequest() {
        return request;
    }

    public void setRequest(CommandRequestResponse request) {
        this.request = request;
    }

    public CommandRequestResponse getResponse() {
        return response;
    }

    public void setResponse(CommandRequestResponse response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "CommandDef{" +
                "command='" + command + '\'' +
                ", commandName='" + commandName + '\'' +
                ", request=" + request +
                ", response=" + response +
                '}';
    }

    public static class CommandRequestResponse {
        /**
         * ,
         *     "response": {
         *       "length": 11,
         *       "fields": [
         *         {"field": "controllerNo", "length": 6, "type": "string"},
         *         {"field": "heartBeatSeq", "length": 4, "type": "string"},
         *         {"field": "answer", "length": 1, "type": "byte", "match": "59"}
         *       ]
         *     }
         */
        int length;
        CommandField[] fields;

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public CommandField[] getFields() {
            return fields;
        }

        public void setFields(CommandField[] fields) {
            this.fields = fields;
        }

        @Override
        public String toString() {
            return "CommandRequestResponse{" +
                    "length=" + length +
                    ", fields=" + Arrays.toString(fields) +
                    '}';
        }
    }


    public static class CommandField {
        // {"field": "answer", "length": 1, "type": "byte", "match": "59"}
        String field;
        int length;
        String type;
        String match;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMatch() {
            return match;
        }

        public void setMatch(String match) {
            this.match = match;
        }

        @Override
        public String toString() {
            return "CommandField{" +
                    "field='" + field + '\'' +
                    ", length=" + length +
                    ", type='" + type + '\'' +
                    ", match='" + match + '\'' +
                    '}';
        }
    }
}
