package testing;

import org.junit.Test;

import client.KVStore;
import junit.framework.TestCase;
import common.logic.Value;
import common.messages.KVMessage;
import common.messages.KVMessage.KvStatusType;

public class InteractionTest extends TestCase {

	private KVStore kvClient;
	
	public void setUp() {
		kvClient = new KVStore();
		try {
			kvClient.connect();
		} catch (Exception e) {
		}
	}

	public void tearDown() {
		kvClient.disconnect();
	}
	
	@Test
	public void testPut() {
		String key = "foobar";
		String value = "bar";
		KVMessage response = null;
		Exception ex = null;

		try {
			response = kvClient.put(key, new Value(value));
		} catch (Exception e) {
			ex = e;
		}
		
		assertTrue(ex == null && response.getStatus() == KvStatusType.PUT_SUCCESS);
	}
	
	@Test
	public void testPutDisconnected() {
		kvClient.disconnect();
		String key = "foo";
		String value = "bar";
		Exception ex = null;

		try {
			kvClient.put(key, new Value(value));
		} catch (Exception e) {
			ex = e;
		}

		assertNotNull(ex);
	}

	@Test
	public void testUpdate() {
		String key = "updateTestValue";
		String initialValue = "initial";
		String updatedValue = "updated";
		
		KVMessage response = null;
		Exception ex = null;

		try {
			kvClient.put(key, new Value(initialValue));
			response = kvClient.put(key, new Value(updatedValue));
			
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex == null && response.getStatus() == KvStatusType.PUT_UPDATE
				&& response.getValue().equals(updatedValue));
	}
	
	@Test
	public void testDelete() {
		String key = "deleteTestValue";
		String value = "toDelete";
		
		KVMessage response = null;
		Exception ex = null;

		try {
			kvClient.put(key, new Value(value));
			response = kvClient.put(key, new Value("null"));
			
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex == null && response.getStatus() == KvStatusType.DELETE_SUCCESS);
	}
	
	@Test
	public void testGet() {
		String key = "foo";
		String value = "bar";
		KVMessage response = null;
		Exception ex = null;

			try {
				kvClient.put(key, new Value(value));
				response = kvClient.get(key);
			} catch (Exception e) {
				ex = e;
			}
		
		assertTrue(ex == null && response.getValue().equals("bar"));
	}

	@Test
	public void testGetUnsetValue() {
		String key = "an unset value";
		KVMessage response = null;
		Exception ex = null;

		try {
			response = kvClient.get(key);
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex == null && response.getStatus() == KvStatusType.GET_ERROR);
	}
	
	@Test
	public void testDeleteNonExistingKvPair() {
		String key = "keyForNull";
		String value = "null";
		Exception ex = null;
		KVMessage response = null;
		
		try {
			response = kvClient.put(key, new Value(value));
		} catch (Exception e) {
			ex = e;
		}
		
		assertTrue(ex == null && response.getStatus() == KvStatusType.DELETE_ERROR);
	}

}
