package io.vantiq.ext.tcp.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CommandJsonTest {
    private static final Logger LOG = LoggerFactory.getLogger(PacketConverterTest.class);

    @Test
    public void testCommandToJson() throws JsonProcessingException {
        CommandDef cmd = new CommandDef();
        cmd.command = "4C5E";
        cmd.commandName = "TestName";
        CommandDef.CommandRequestResponse request = new CommandDef.CommandRequestResponse();
        CommandDef.CommandRequestResponse response = new CommandDef.CommandRequestResponse();
        cmd.request = request;
        cmd.response = response;

        request.length = 1;
        request.fields = new CommandDef.CommandField[1];
        CommandDef.CommandField field1 = new CommandDef.CommandField();
        field1.length = 1;
        field1.field = "TEST";
        field1.type = "byte";
        request.fields[0] = field1;

        response.length = 2;
        response.fields = new CommandDef.CommandField[1];
        CommandDef.CommandField field2 = new CommandDef.CommandField();
        field2.length = 2;
        field2.field = "ANSWER";
        field2.type = "byte";
        response.fields[0] = field2;


        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, CommandDef.class);
        String result = mapper.writeValueAsString(cmd);
        LOG.debug("Command definition:{}", result);
    }

    @Test
    public void testJsonToCmd() {
        CommandConverter converter = new CommandConverter();
        LOG.debug("Command definition map:{}", converter.commandMap);
    }
}
