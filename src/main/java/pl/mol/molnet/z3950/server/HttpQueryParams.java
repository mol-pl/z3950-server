package pl.mol.molnet.z3950.server;

/**
 * Parametry zapytania do Molnet.
 *
 * @author Paweł
 */
public class HttpQueryParams {

	private String title;
	private String author;
	private String isbn;
	private String publDate;
	private String bibliographicDbAlias;
	private String tenantUrl;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the isbn
	 */
	public String getIsbn() {
		return isbn;
	}

	/**
	 * @param isbn the isbn to set
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	/**
	 * @return the publDate
	 */
	public String getPublDate() {
		return publDate;
	}

	/**
	 * @param publDate the publDate to set
	 */
	public void setPublDate(String publDate) {
		this.publDate = publDate;
	}

	/**
	 * @return the bibliographicDbAlias
	 */
	public String getBibliographicDbAlias() {
		return bibliographicDbAlias;
	}

	/**
	 * @param bibliographicDbAlias the bibliographicDbAlias to set
	 */
	public void setBibliographicDbAlias(String bibliographicDbAlias) {
		this.bibliographicDbAlias = bibliographicDbAlias;
	}

	/**
	 * @return the tenantUrl
	 */
	public String getTenantUrl() {
		return tenantUrl;
	}

	/**
	 * @param tenantUrl the tenantUrl to set
	 */
	public void setTenantUrl(String tenantUrl) {
		this.tenantUrl = tenantUrl;
	}
}
