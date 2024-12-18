	package com.example.myapplication.assignment;


	import com.example.myapplication.assignment.exceptions.ServerCommunicationError;

	import java.text.ParseException;
	import java.util.ArrayList;
	import java.util.Date;
	import java.util.Hashtable;
	import java.util.List;

	import org.json.simple.JSONObject;


	public class Article extends ModelEntity{

		private String titleText;
		private String category;
		private String abstractText;
		private String bodyText;
		private String footerText;
		private int idUser;
		private Image mainImage;
		private String imageDescription;
		private String thumbnail;
    private String subtitleText;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		private String username;

		public Date getUpdate_date() {
			return update_date;
		}

		public void setUpdate_date(Date update_date) {
			this.update_date = update_date;
		}

		private Date update_date;

		private String parseStringFromJson(JSONObject jsonArticle, String key, String def){
			Object in = jsonArticle.getOrDefault(key,def);
			return (in==null?def:in).toString();
		}

		@SuppressWarnings("unchecked")
		public Article(ModelManager mm, JSONObject jsonArticle){
			super(mm);
			try{
				id = Integer.parseInt(jsonArticle.get("id").toString());
				idUser = Integer.parseInt(parseStringFromJson(jsonArticle,"id_user","0"));
				titleText = parseStringFromJson(jsonArticle,"title","").replaceAll("\\\\","");
        subtitleText = parseStringFromJson(jsonArticle,"subtitle","").replaceAll("\\\\","");
        category = parseStringFromJson(jsonArticle,"category","").replaceAll("\\\\","");
				abstractText = parseStringFromJson(jsonArticle,"abstract","").replaceAll("\\\\","");
				bodyText = parseStringFromJson(jsonArticle,"body","").replaceAll("\\\\","");
				footerText = parseStringFromJson(jsonArticle,"footer","").replaceAll("\\\\","");

				imageDescription = parseStringFromJson(jsonArticle,"image_description","").replaceAll("\\\\","");
				thumbnail = parseStringFromJson(jsonArticle,"thumbnail_image","").replaceAll("\\\\","");

				update_date = Utils.dateFromString(parseStringFromJson(jsonArticle,"update_date","").replaceAll("\\\\",""));
				username = parseStringFromJson(jsonArticle,"username","").replaceAll("\\\\","");

				String imageData = parseStringFromJson(jsonArticle,"image_data","").replaceAll("\\\\","");

				if (imageData!=null && !imageData.isEmpty())
					mainImage = new Image(mm, 1, imageDescription, id, imageData);
			}catch(Exception e){
				Logger.log(Logger.ERROR, "ERROR: Error parsing Article: from json"+jsonArticle+"\n"+e.getMessage());
				throw new IllegalArgumentException("ERROR: Error parsing Article: from json"+jsonArticle);
			}
		}

		public Article(ModelManager mm, String category, String titleText, String subtitleText, String abstractText, String body, String footer, Date date){
			super(mm);
			id = -1;
			this.category = category;
			//idUser = Integer.parseInt(mm.getIdUser());
			this.abstractText = abstractText ;
			this.titleText = titleText;
      this.subtitleText=subtitleText;
			bodyText = body;
			footerText = footer;
			update_date = date;
		}

		public void setId(int id){
			if (id <1){
				throw new IllegalArgumentException("ERROR: Error setting a wrong id to an article:"+id);
			}
			if (this.id>0 ){
				throw new IllegalArgumentException("ERROR: Error setting an id to an article with an already valid id:"+this.id);
			}
			this.id = id;
		}

		public String getTitleText() {
			return titleText;
		}
    public String getSubtitleText() {return subtitleText;}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category= category;
		}
		public void setTitleText(String titleText) {
			this.titleText = titleText;
		}
    public void setSubtitleText(String subtitleText) {
      this.subtitleText = subtitleText;
    }
		public String getAbstractText() {
			return abstractText;
		}
		public void setAbstractText(String abstractText) {
			this.abstractText = abstractText;
		}
		public String getBodyText() {
			return bodyText;
		}
		public void setBodyText(String bodyText) {
			this.bodyText = bodyText;
		}
		public String getFooterText() {
			return footerText;
		}
		public void setFooterText(String footerText) {
			this.footerText = footerText;
		}

		public int getIdUser(){
			return idUser;
		}
		public Image getImage() throws ServerCommunicationError {
			Image image = mainImage;
			if (mainImage==null && thumbnail!=null && !thumbnail.isEmpty()){
				image = new Image(mm,1,"",getId(),thumbnail);
			}
			return image;
		}

		public String getThumbnail(){
			return this.thumbnail;
		}
		public void setThumbnail(String thumbnail){
			this.thumbnail = thumbnail;
		}


		public void setImage(Image image) {
			this.mainImage = image;
		}

		public Image addImage(String b64Image, String description) throws ServerCommunicationError {
			int order = 1;
			Image img =new Image(mm, order, description, getId(), b64Image);
			mainImage= img;
			return img;
		}

		@Override
		public String toString() {
			return "Article [id=" + getId()
					//+ "isPublic=" + isPublic + ", isDeleted=" + isDeleted
					+", titleText=" + titleText
          +", subtitleText=" + subtitleText
					+", abstractText=" + abstractText
					+  ", bodyText="	+ bodyText + ", footerText=" + footerText
					//+ ", publicationDate=" + Utils.dateToString(publicationDate)
					+", image_description=" + imageDescription
					+", image_data=" + mainImage
					+", thumbnail=" + thumbnail
					+ "]";
		}

		public Hashtable<String,String> getAttributes(){
			Hashtable<String,String> res = new Hashtable<String,String>();
			//res.put("is_public", ""+(isPublic?1:0));
			//res.put("id_user", ""+idUser);
			res.put("category", category);
			res.put("abstract", abstractText);
			res.put("title", titleText);
      res.put("subtitle", subtitleText);
			//res.put("is_deleted", ""+(isDeleted?1:0));
			res.put("body", bodyText);
			res.put("footer", footerText);
			res.put("update_date", Utils.dateToString(update_date));
			if (mainImage!=null){
				res.put("image_data", mainImage.getImage());
				res.put("image_media_type", "image/png");
			}

			if (mainImage!=null && mainImage.getDescription()!=null && !mainImage.getDescription().isEmpty())
				res.put("image_description", mainImage.getDescription());
			else if (imageDescription!=null && !imageDescription.isEmpty())
				res.put("image_description", imageDescription);

			//res.put("publication_date", publicationDate==null?null:Utils.dateToString(publicationDate));
			return res;
		}
	}
