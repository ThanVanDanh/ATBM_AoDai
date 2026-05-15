package filter;

import dao.CategoryDao;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import model.product.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebFilter("/*")
public class CategoryMenuFilter implements Filter {

	private CategoryDao categoryDao;

	@Override
	public void init(FilterConfig filterConfig) {
		this.categoryDao = new CategoryDao();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {


		if (request instanceof HttpServletRequest httpRequest) {
			String requestURI = httpRequest.getRequestURI();
			if (requestURI.contains("/admin/")) {
				chain.doFilter(request, response);
				return;
			}
			ServletContext servletContext = httpRequest.getServletContext();

			Object cachedAll = servletContext.getAttribute("menuAllCategories");
			Object cachedChildren = servletContext.getAttribute("menuChildren");

			boolean needsLoad = cachedAll == null || cachedChildren == null;
			if (!needsLoad && cachedAll instanceof List<?> list && list.isEmpty()) needsLoad = true;
			if (!needsLoad && cachedChildren instanceof Map<?, ?> map && map.isEmpty()) needsLoad = true;

			if (needsLoad) {
				try {
					List<Category> all = categoryDao.getAll();
					Map<Integer, List<Category>> childrenByParent = buildChildrenByParent(all);

					servletContext.setAttribute("menuAllCategories", all);
					servletContext.setAttribute("menuChildren", childrenByParent);
				} catch (Exception e) {
					if (servletContext.getAttribute("menuAllCategories") == null) {
						servletContext.setAttribute("menuAllCategories", Collections.emptyList());
					}
					if (servletContext.getAttribute("menuChildren") == null) {
						servletContext.setAttribute("menuChildren", Collections.emptyMap());
					}
				}
			}

			request.setAttribute("menuAllCategories", servletContext.getAttribute("menuAllCategories"));
			request.setAttribute("menuChildren", servletContext.getAttribute("menuChildren"));
		}

		chain.doFilter(request, response);
	}

	private Map<Integer, List<Category>> buildChildrenByParent(List<Category> categories) {
		Map<Integer, List<Category>> childrenByParent = new HashMap<>();
		if (categories == null) return childrenByParent;

		for (Category category : categories) {
			if (category == null) continue;
			Integer parentId = category.getParentId();
			int parentKey = (parentId == null) ? 0 : parentId;

			childrenByParent.computeIfAbsent(parentKey, k -> new ArrayList<>()).add(category);
		}
		return childrenByParent;
	}
}
