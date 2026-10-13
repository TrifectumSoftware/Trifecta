package trifectumsoftware.trifecta.network.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import trifectumsoftware.trifecta.client.ChatNotifier;
import trifectumsoftware.trifecta.network.NetworkHandler;
import trifectumsoftware.trifecta.script.ScriptHash;

public class MessageScriptHash implements IMessage {

    private String hash;
    private int fileCount;

    public MessageScriptHash() {}

    public MessageScriptHash(String hash, int fileCount) {
        this.hash = hash;
        this.fileCount = fileCount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hash = ByteBufUtils.readUTF8String(buf);
        fileCount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, hash);
        buf.writeInt(fileCount);
    }

    public static class Handler implements IMessageHandler<MessageScriptHash, IMessage> {

        @Override
        public IMessage onMessage(MessageScriptHash message, MessageContext ctx) {
            if (!NetworkHandler.isScriptHashAvailable()) {
                return null;
            }
            String localHash = ScriptHash.hash();
            if (localHash.equals(message.hash)) {
                return null;
            }
            String text = "Scripts differ from the server (client: " + ScriptHash.fileCount()
                + " file(s), md5 "
                + localHash
                + " / server: "
                + message.fileCount
                + " file(s), md5 "
                + message.hash
                + "), scripted content may not match, or maybe harmful! Please be aware of malware. See logs/groovy.log.";
            ChatNotifier.warn(text);
            return null;
        }
    }
}
