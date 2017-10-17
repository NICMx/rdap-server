package mx.nic.rdap.server.renderer;

import java.util.Objects;

import mx.nic.rdap.renderer.Renderer;

/**
 * class that stores a renderer and maps it to a MIME type.
 */
public class RendererWrapper {

	/**
	 * Renderer that hold this wrapper.
	 */
	private Renderer renderer;

	/**
	 * MIME type mapped to <code>renderer</code>.
	 */
	private String mimeType;

	public RendererWrapper(Renderer renderer, String mimeType) {
		Objects.nonNull(renderer);
		Objects.nonNull(mimeType);
		this.mimeType = mimeType;
		this.renderer = renderer;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
		result = prime * result + ((renderer == null) ? 0 : renderer.hashCode());
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
		RendererWrapper other = (RendererWrapper) obj;
		if (mimeType == null) {
			if (other.mimeType != null)
				return false;
		} else if (!mimeType.equals(other.mimeType))
			return false;
		if (renderer == null) {
			if (other.renderer != null)
				return false;
		} else if (!renderer.equals(other.renderer))
			return false;
		return true;
	}

}
