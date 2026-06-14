package controller;

import dao.BannerDao;
import dao.CategoryDao;
import dao.HomeDao;
import dao.ProductDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.banner.Banner;
import model.home.Home;
import model.home.SectionDTO;
import model.home.TabDTO;
import model.product.Category;
import model.product.ProductListDTO;
import services.CloudinaryService;
import services.ProductService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "HomeController", urlPatterns = { "/home" })
public class HomeController extends HttpServlet {

    private ProductService productService;
    private CategoryDao categoryDao;
    private HomeDao homeDao;
    private BannerDao bannerDao;

    @Override
    public void init() throws ServletException {
        try {
            this.productService = new ProductService(new ProductDao().get(), new CloudinaryService());
            this.categoryDao = new CategoryDao();
            this.homeDao = new HomeDao();
            this.bannerDao = new BannerDao();
        } catch (Exception ex) {
            System.err.println(" Failed to initialize HomeController: " + ex.getMessage());
            ex.printStackTrace();
            throw new ServletException("Cannot initialize HomeController", ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            List<Banner> banners = bannerDao.getActiveBanners();
            req.setAttribute("banners", banners);

            List<SectionDTO> dynamicSections = loadDynamicSections();
            req.setAttribute("dynamicSections", dynamicSections);

            req.getRequestDispatcher("/index.jsp").forward(req, resp);

        } catch (Exception e) {
            System.err.println(" Error in HomeController: " + e.getMessage());
            e.printStackTrace();

            req.setAttribute("banners", new ArrayList<Banner>());
            req.setAttribute("dynamicSections", new ArrayList<SectionDTO>());

            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }

    private List<SectionDTO> loadDynamicSections() {
        List<String> sectionKeys = homeDao.getAllSectionKeys();
        List<SectionDTO> sections = new ArrayList<>();

        for (String key : sectionKeys) {
            try {
                SectionDTO section = loadSection(key);
                if (section != null && section.title != null && !section.tabs.isEmpty()) {
                    sections.add(section);
                    System.out.println(" Loaded section: " + key + " with " + section.tabs.size() + " tabs");
                } else {
                    System.out.println(" Skipped section: " + key + " (no data)");
                }
            } catch (Exception e) {
                System.err.println("Error loading section " + key + ": " + e.getMessage());
            }
        }

        return sections;
    }

    private SectionDTO loadSection(String sectionKey) {
        String title = homeDao.getSectionTitle(sectionKey);
        List<Home> dbTabs = homeDao.getSectionTabs(sectionKey);

        if (title == null || dbTabs == null || dbTabs.isEmpty()) {
            return null;
        }

        SectionDTO section = new SectionDTO();
        section.key = sectionKey;
        section.title = title;

        for (Home homeTab : dbTabs) {
            Category category = categoryDao.getById(homeTab.getCategoryId());
            if (category == null) {
                System.err.println("Category not found: " + homeTab.getCategoryId());
                continue;
            }

            TabDTO tab = new TabDTO();
            tab.index = homeTab.getPosition();
            tab.title = category.getNameCategory();

            List<ProductListDTO> products = productService.getProductsByCategory(category.getId());

            tab.products = products.size() > 5 ? products.subList(0, 5) : products;
            
            section.tabs.add(tab);
            System.out.println(" Tab " + tab.index + ": " + tab.title + " (" + tab.products.size() + " products)");
        }

        return section;
    }
}
