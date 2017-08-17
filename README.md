# toy_template_engine
This is a stuipid template engine!    
**Warming:** Using these code may destroy your project!^_^

# Basic Usage 
First set the template file directory in class chzone.template_engine.utils.ConstantValue, stupid design!
```
// The template file will be placed in the directory
public static String TEMPLATE_FILE_PATH = "src/test/resources/template";
```
Creating a template like this:
```
<html>
<head>
	<title>{{ title }}</title>
</head>
	<body>
		<p>Supported Syntax:</p>
		{% if !syntaxList.isEmpty %}
		<ul>
		{% for syntax in syntaxList %}
			{% if !syntax.isEmpty %}
		    <li>{{ syntax }}</li>
		    {% endif %}
		{% endfor %}
		</ul>
		{% endif %}
	</body>
</html>
```
Java code 
```
TemplateConstructor tc = new TemplateConstructor("template.html");
AbstractTemplate at = tc.getTemplate();
Map<String,Object> context = new HashMap<String,Object>();
List<String> syntaxList = new ArrayList<String>();
context.put("title", "stupid toy template engine");
syntaxList.add("{{value}}");
syntaxList.add("{% if expression %} .... {% endif %}");
syntaxList.add("{% for item in  collection %} .... {% end for %}");
context.put("syntaxList", syntaxList);
System.out.println(at.render(context));
```
the output:
```html
<html>
<head>
    <title>stupid toy template engine</title>
</head>
<body>
    <p>Supported Syntax:</p>
    <ul>
        <li>{{value}}</li>
        <li>{% if expression %} .... {% endif %}</li>
        <li>{% for item in  collection %} .... {% end for %}</li>
    </ul>
</body>
</html>


```
