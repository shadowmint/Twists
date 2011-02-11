/**
 * Copyright 2010 Douglas Linder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package twists.client.components.ui.frame;

import java.util.HashMap;

import twisted.client.Component;
import twisted.client.ComponentApi;
import twisted.client.ComponentContainer;
import twisted.client.ComponentLog;
import twisted.client.impl.ComponentQuery;

import com.google.gwt.dom.client.Element;
import twisty.client.utils.CssFactory;
import twisty.client.utils.Stylesheet;
import twisty.client.utils.Xml;

/** Creates a nice image-bg frame around the target element. */
public class Frame extends Component {

	/** The ID for this component, for internal use. */
	private static String ID = "Frame";

	/** General template for this component. */
	private static Xml template = null;

	/** General stylesheet for this component. */
	private static Stylesheet sheet = null;

	public Frame(ComponentContainer root) {
		super(root);
	}

	@Override
	public ComponentApi api() {
		return null;
	}

	@Override
	public void init() {
		requireAsset("Frame");   // The frame to style up.
		requireValue("Base");    // Base url.
		requireValue("Key");     // Key for this url.
		requireValue("Type");    // File type postfix (eg. png)
		requireValue("Padding"); // Padding size in pixels.
	}

	@Override
	public void run() {

		// Get the template
		if (template == null) {
			template = new Xml();
			template.parse(FrameBundle.INSTANCE.FrameHtml().getText());
		}

		// Get the image base
		String base_url = root.getValue("Base");
		String style_key = root.getValue("Key");
		String xtn = root.getValue("Type");
		int padding = 0;
		try {
			padding = Integer.parseInt(root.getValue("Padding"));
		}
		catch(Exception e) {
		}

		// Generate the style tags
		HashMap<String, String> styles = new HashMap<String, String>();
		if (sheet == null) {
			String rawCss = FrameBundle.INSTANCE.FrameCss().getText();
			try {
				sheet = Stylesheet.createStylesheet(rawCss);
			}
			catch(Exception err) {
				ComponentLog.trace("Invalid core css for ComponentId-"+ID);
				ComponentLog.trace(err.toString());
			}
		}
		if (sheet != null) {
			String prefix = "Component-"+ID+"-";
			String base_style = prefix + style_key;
			styles.put("Namespace", prefix);
			if(!sheet.has(base_style)) {

				// Place holder to mark this as created.
				sheet.set(prefix + style_key, "");

				// Create individual rule sets
				String sections[]            = {"TopLeft", "Top", "TopRight", "Left", "Center", "Right", "BottomLeft", "Bottom", "BottomRight"};
				boolean section_full_height[] = { false, false, false, true, true, true, false, false, false };
				boolean section_full_width[] =  { false, true, false, false, true, false, false, true, false };
				for (int i = 0; i < sections.length; ++i) {
					String url = base_url + sections[i] + "." + xtn;
					String css_target = base_style + "-" + sections[i];
					styles.put(sections[i], css_target);

					// Dynamic sizes too~
					String new_style = "background-image: url("+url+") !important;";
					if(!section_full_width[i])
						new_style += "width: " + padding + "px;";
					if(!section_full_height[i])
						new_style += "height: " + padding + "px;";

					sheet.set("." + css_target, new_style);
				}
			}

			// Grab the assets to use
			HashMap<String, Element> assets = new HashMap<String, Element>();
			assets.put("Frame", root.getAsset("Frame"));

			// Create a new content node to replace frame.
			Element e = null;
			try {
				e = CssFactory.createCssBlock(template, styles, assets);
			}
			catch(Exception err) {
				ComponentLog.trace("ComponentId-"+ID+" :: Failed to create block: " + err.toString());
				e = null;
			}

			// Bind and reattach~
			if (e != null) {
				try {
					Element frame = root.getAsset("Frame");
					root.getElement().appendChild(e);

					String selector = prefix + "Center";
					ComponentQuery result = ComponentQuery.query(selector, e);
					Element innerFrame = result.getItem(0);
					innerFrame.appendChild(frame);
					e.setClassName(e.getClassName() + " " + base_style + " Component-"+ID);
				}
				catch(Exception error) {
					ComponentLog.trace("Failed to deploy new ComponentId-"+ID+": " + error.toString());
				}
			}
		}

		ComponentLog.trace("Frame created.");
		complete();
	}
}
