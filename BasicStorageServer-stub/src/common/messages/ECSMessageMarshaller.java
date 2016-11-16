package common.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import common.logic.KVServerItem;
import common.messages.ECSMessage.EcsStatusType;

public class ECSMessageMarshaller extends Marshaller<ECSMessage> {
	
	public byte[] marshal(ECSMessage message) {
		StringBuilder stringBuilder = new StringBuilder();	
		String type = message.getStatus().toString();
		
		stringBuilder.append(type);
		stringBuilder.append(UNIT_SEPARATOR);
		if (message.getStatus().equals(EcsStatusType.META_DATA_TABLE)) {
			stringBuilder.append(convertMetaDataTableToString(message.getMetaDataTable()));
		} else if (message.getStatus().equals(EcsStatusType.UPDATE_START_INDEX)) {
			stringBuilder.append(new String(message.getStartIndex(), CHARSET));
		} else if (message.getStatus().equals(EcsStatusType.SERVER_START_END_INDEX)) {
			stringBuilder.append(new String(message.getStartIndex(), CHARSET));
			stringBuilder.append(UNIT_SEPARATOR);
			stringBuilder.append(new String(message.getEndIndex(), CHARSET));
		}		
		stringBuilder.append(CARRIAGE);
		return stringBuilder.toString().getBytes(CHARSET);
	};
	
	private String convertMetaDataTableToString(List<KVServerItem> metaDataTable) {
		StringBuilder stringBuilder = new StringBuilder();
		ListIterator<KVServerItem> iterator = metaDataTable.listIterator();
		while (iterator.hasNext()) {
			String serverString = convertKVServerItemToString(iterator.next());
			stringBuilder.append(serverString);
			if (iterator.hasNext()) stringBuilder.append(UNIT_SEPARATOR);
		}
		return stringBuilder.toString();
	}
	
	
	public ECSMessageItem unmarshal(byte[] message){
		String[] messageTokens = getMessageTokens(message);
		ECSMessageItem messageItem = new ECSMessageItem(EcsStatusType.valueOf(messageTokens[0]));
		if (messageTokens[0].equals(EcsStatusType.META_DATA_TABLE.toString())) {
			messageItem.setMetaDataTable(convertStringToMetaDataTable(messageTokens));
		} else if (messageTokens[0].equals(EcsStatusType.SERVER_START_END_INDEX.toString())) {
			messageItem.setStartIndex(messageTokens[1].getBytes(CHARSET));
			messageItem.setEndIndex(messageTokens[2].getBytes(CHARSET));
		} else if (messageTokens[0].equals(EcsStatusType.UPDATE_START_INDEX.toString())) {
			messageItem.setStartIndex(messageTokens[1].getBytes(CHARSET));
		}
		return messageItem;
	}	
	
	private List<KVServerItem> convertStringToMetaDataTable(String[] messageTokens)  {
		List<KVServerItem> metaDataTable = new ArrayList<>();
		for (int i=1; i<messageTokens.length; i++) {
			KVServerItem kvServer = convertStringToMetaDataTableServer(messageTokens[i]);
			metaDataTable.add(kvServer);
		}
		return metaDataTable;
	}
	
}
