package server;

/**
 * The type Response.
 */
public class Response {
  private String message;
  private String key;
  private String value;

  /**
   * Instantiates a new Response with the given message, key and value.
   *
   * @param message the message about the response
   * @param key  the key involved in the operation
   * @param value the value associated with the key
   */
  public Response(String message, String key, String value) {
    if (message == null || key == null) {
      throw new IllegalArgumentException("Message and key cannot be null.");
    }
    this.message = message;
    this.key = key;
    this.value = value;
  }

  /**
   * Formatted response as a string
   * If the value is null or empty, the value part is omitted.
   * @return the formatted response
   */
  public String formattedResponse() {
    StringBuilder sb = new StringBuilder();
    sb.append("Message: ").append(message);
    sb.append(" Key: ").append(key);
    if(value != null && !value.isEmpty()) {
      sb.append(" Value: ").append(value);
    }
    return sb.toString();
  }
}
