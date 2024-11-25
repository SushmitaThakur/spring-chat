package com.friday.chat.dto;

public class MessageResponseDTO {

    private String groupName;
    private String messageContent;

    // Constructor
    public MessageResponseDTO(String groupName, String messageContent) {
        this.groupName = groupName;
        this.messageContent = messageContent;
    }

    // Getters and setters
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
