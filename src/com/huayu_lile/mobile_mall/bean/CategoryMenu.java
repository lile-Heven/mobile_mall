package com.huayu_lile.mobile_mall.bean;

import java.io.Serializable;
import java.util.List;

public class CategoryMenu {
	String category;
	List<CategoryItem> categoryitem;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<CategoryItem> getCategoryitem() {
		return categoryitem;
	}

	public void setCategoryitem(List<CategoryItem> categoryitem) {
		this.categoryitem = categoryitem;
	}

	public class CategoryItem implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1110088226879892357L;
		String typename;
		String imgurl;
		List<Menu> menu;

		public String getTypename() {
			return typename;
		}

		public void setTypename(String typename) {
			this.typename = typename;
		}

		public String getImgurl() {
			return imgurl;
		}

		public void setImgurl(String imgurl) {
			this.imgurl = imgurl;
		}

		public List<Menu> getMenu() {
			return menu;
		}

		public void setMenu(List<Menu> menu) {
			this.menu = menu;
		}

		public class Menu implements Serializable{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1489205266935237283L;
			String menuname;
			List<MenuItem> menuitem;

			public String getMenuname() {
				return menuname;
			}

			public void setMenuname(String menuname) {
				this.menuname = menuname;
			}

			public List<MenuItem> getMenuitem() {
				return menuitem;
			}

			public void setMenuitem(List<MenuItem> menuitem) {
				this.menuitem = menuitem;
			}

			public class MenuItem implements Serializable{
				/**
				 * 
				 */
				private static final long serialVersionUID = 1533372926712529495L;
				String item;

				public String getItem() {
					return item;
				}

				public void setItem(String item) {
					this.item = item;
				}
			}
		}
	}
}
