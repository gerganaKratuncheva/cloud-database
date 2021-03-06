package testing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import common.logic.KVServerItem;
import common.logic.Value;
import common.messages.ECSMessage.EcsStatusType;
import common.messages.ECSMessageItem;
import common.messages.KVMessage;
import common.messages.KVMessageItem;
import common.messages.KVMessage.KvStatusType;
import common.messages.Marshaller;
import junit.framework.TestCase;

public class AdditionalTest extends TestCase {
	
	Marshaller marshaller = new Marshaller();

	@Test
	public void testMarshalAndUnmarshalGetMessage() {
		KVMessageItem message = new KVMessageItem(KvStatusType.GET, "12345");
		byte[] marshaledMessage = marshaller.marshal(message);
		KVMessage unmarshaledMessage = (KVMessageItem) marshaller.unmarshal(marshaledMessage);
		assertTrue(unmarshaledMessage.getStatus().equals(KvStatusType.GET));
		assertTrue(unmarshaledMessage.getKey().equals("12345"));
		assertNull(unmarshaledMessage.getValue());
	}
	
	@Test
	public void testMarshalAndUnmarshalGetSuccessMessage() {
		KVMessageItem message = new KVMessageItem(KvStatusType.GET_SUCCESS, "foundValue");
		byte[] marshaledMessage = marshaller.marshal(message);
		KVMessage unmarshaledMessage = (KVMessageItem) marshaller.unmarshal(marshaledMessage);
		assertTrue(unmarshaledMessage.getStatus().equals(KvStatusType.GET_SUCCESS));
		assertNull(unmarshaledMessage.getKey());
		assertTrue(unmarshaledMessage.getValue().equals("foundValue"));
	}
	
	@Test
	public void testMarshalAndUnmarshalPutMessage() {
		KVMessageItem message = new KVMessageItem(KvStatusType.PUT, "123", new Value(1, 
				new SimpleDateFormat("yyyy/MM/dd_HH/mm/ss/SS").format(Calendar.getInstance().getTime()), "value"));
		byte[] marshaledMessage = marshaller.marshal(message);
		KVMessage unmarshaledMessage = (KVMessageItem) marshaller.unmarshal(marshaledMessage);
		assertTrue(unmarshaledMessage.getStatus().equals(KvStatusType.PUT));
		assertTrue(unmarshaledMessage.getKey().equals("123"));
		assertTrue(unmarshaledMessage.getValue().equals("value"));
	}
	
	@Test
	public void testMarshalAndUnmarshalPutSuccessMessage() {
		KVMessageItem message = new KVMessageItem(KvStatusType.PUT_SUCCESS);
		byte[] marshaledMessage = marshaller.marshal(message);
		KVMessage unmarshaledMessage = (KVMessageItem) marshaller.unmarshal(marshaledMessage);
		assertTrue(unmarshaledMessage.getStatus().equals(KvStatusType.PUT_SUCCESS));
		assertNull(unmarshaledMessage.getKey());
		assertNull(unmarshaledMessage.getValue());
	}
	
	@Test
	public void testECSMessageMarshallerWithStartIndex() {
		ECSMessageItem message = new ECSMessageItem(EcsStatusType.UPDATE_START_INDEX, new byte[]{12, -3, 4});
		ECSMessageItem unmarshalledMessage = marshalAndUnmarshalMessage(message);
		assertTrue(unmarshalledMessage.getStatus().equals(EcsStatusType.UPDATE_START_INDEX));
		assertTrue(Arrays.equals(unmarshalledMessage.getStartIndex(), new byte[]{12, -3, 4}));
	}
	
	@Test
	public void testECSMessageMarshallerWithStart() {
		ECSMessageItem message = new ECSMessageItem(EcsStatusType.START);
		ECSMessageItem unmarshalledMessage = marshalAndUnmarshalMessage(message);
		assertTrue(unmarshalledMessage.getStatus().equals(EcsStatusType.START));
	}
	
	@Test
	public void testECSMessageMarshallerWithServerStartEndIndex() {
		ECSMessageItem message = new ECSMessageItem(EcsStatusType.SERVER_START_END_INDEX, new byte[]{-15, 123, 51}, new byte[]{-19, 112, 12});
		ECSMessageItem unmarshalledMessage = marshalAndUnmarshalMessage(message);
		assertTrue(unmarshalledMessage.getStatus().equals(EcsStatusType.SERVER_START_END_INDEX));
		assertTrue(Arrays.equals(unmarshalledMessage.getStartIndex(), (new byte[] {-15, 123, 51})));
		assertTrue(Arrays.equals(unmarshalledMessage.getEndIndex(), (new byte[] {-19, 112, 12})));
	}
	
	@Test
	public void testECSMessageMarshallerWithMetaDataTable() {
		KVServerItem server1 = new KVServerItem("server1", "localhost", "50000");
		server1.setStartIndex(new byte[]{56, -22, 0, 12, 44});
		server1.setEndIndex(new byte[]{94, 32, -1, 4});
		KVServerItem server2 = new KVServerItem("server2", "123.123.13.4", "50012");
		server2.setStartIndex(new byte[]{-2, -52, 13, 53});
		server2.setEndIndex(new byte[]{42, 12, 13, 62});
		List<KVServerItem> metaDataTable = new ArrayList<>(Arrays.asList(server1, server2));
		ECSMessageItem message = new ECSMessageItem(EcsStatusType.META_DATA_TABLE, metaDataTable);
		ECSMessageItem unmarshalledMessage = marshalAndUnmarshalMessage(message);
		assertTrue(unmarshalledMessage.getStatus().equals(EcsStatusType.META_DATA_TABLE));
		server1 = unmarshalledMessage.getMetaDataTable().get(0);
		assertTrue(server1.getName().equals("server1") && server1.getIp().equals("localhost") && server1.getPort().equals("50000"));
		assertTrue(Arrays.equals(server1.getStartIndex(), new byte[]{56, -22, 0, 12, 44}));
		assertTrue(Arrays.equals(server1.getEndIndex(), new byte[]{94, 32, -1, 4}));
		server2 = unmarshalledMessage.getMetaDataTable().get(1);
		assertTrue(server2.getName().equals("server2") && server2.getIp().equals("123.123.13.4") && server2.getPort().equals("50012"));
		assertTrue(Arrays.equals(server2.getStartIndex(), new byte[]{-2, -52, 13, 53}));
		assertTrue(Arrays.equals(server2.getEndIndex(), new byte[]{42, 12, 13, 62}));
	}
	
	private ECSMessageItem marshalAndUnmarshalMessage(ECSMessageItem message) {
		byte[] marshaledMessage = marshaller.marshal(message);
		return (ECSMessageItem) marshaller.unmarshal(marshaledMessage);
	}
}
