package com.pocketsoap.salesforce.rest.chatter;


/**
 * A piece of text in a Chatter post or comment
 * @author nlipke-sfdc
 *
 */
public class TextMessageSegment implements MessageSegment {
	/** The text type */
	private static final String TYPE_TEXT = "Text";
	
	/**
	 * The text
	 */
	private String text;
	
	/**
	 * Create Segment with null text
	 */
	public TextMessageSegment() {
	}
	
	/**
	 * Create a segment with the specified text
	 * @param text the text
	 */
	public TextMessageSegment(String text) {
		this.text = text;
	}

	/**
	 * Get the text
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set the text
	 * @param text the text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * The segments type
	 * @return Will always return <code>"Text"</code>
	 */
	public String getType() {
		return TYPE_TEXT;
	}

	@Override
	public String toString() {
		return "TextMessageSegment [text=" + text + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextMessageSegment other = (TextMessageSegment) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

}
