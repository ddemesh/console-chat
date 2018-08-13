package by.dima.model.logic;

import by.dima.model.entity.Message;
import by.dima.model.entity.Request;
import by.dima.util.JasonObjectConverter;
import by.dima.util.ObjectConverter;
import by.dima.util.UserHolder;

import java.util.Date;

public class CommandPreprocessor implements RequestPreprocessor {

    private static final String DEFAULT_COMMAND_KEY_START = "/";
    private static final String DEFAULT_COMMAND_KEY_END = " ";
    private static final String DEFAULT_MESSAGE_COMMAND = "message";

    private String commandKeyStart;
    private String commandKeyEnd;
    private String messageCommand;

    private ObjectConverter requestObjConverter;

    public CommandPreprocessor() {
        this(DEFAULT_COMMAND_KEY_START, DEFAULT_COMMAND_KEY_END, DEFAULT_MESSAGE_COMMAND);
    }

    public CommandPreprocessor(String commandKeyStart, String commandKeyEnd, String messageCommand) {
        this.commandKeyStart = commandKeyStart;
        this.commandKeyEnd = commandKeyEnd;
        this.messageCommand = messageCommand;

        requestObjConverter = new JasonObjectConverter();
    }

    @Override
    public String process(String data) {
        String result;
        Request request = new Request();
        if (data.startsWith(commandKeyStart)) {
            if (data.indexOf(commandKeyEnd) >= 0) {
                request.setCommand(data.substring(data.indexOf(commandKeyStart) + 1, data.indexOf(commandKeyEnd)));
                request.setData(DataPreprocessorBuilder.build(request.getCommand()).process(data.substring(data.indexOf(commandKeyEnd) + 1)));
            } else {
                request.setCommand(data.substring(data.indexOf(commandKeyStart) + 1));
                request.setData(DataPreprocessorBuilder.build(request.getCommand()).process(""));
            }

        } else {
            request.setCommand(messageCommand);
            request.setData(requestObjConverter.write(new Message(data, new Date(), UserHolder.getInstance().getCurrentUser())));
        }

        result = requestObjConverter.write(request);

        return result;
    }
}