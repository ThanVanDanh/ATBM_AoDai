package services;

import dao.PolicyDao;
import model.policy.Policy;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Objects;

public class PolicyService {
    private final PolicyDao policyDao;
    private final Jdbi jdbi;

    public PolicyService(Jdbi jdbi) {
        this.jdbi = Objects.requireNonNull(jdbi);
        this.policyDao = new PolicyDao();
    }

    public List<Policy> getAllPolicies() {
        return policyDao.getAll();
    }

    public Policy getPolicyById(int id) {
        return policyDao.getById(id);
    }

    public Policy getPolicyByCategoryId(int categoryId) {
        return policyDao.getByCategoryId(categoryId);
    }

    public int createPolicy(Policy policy) {
        if (policy == null) return -1;
        if (policy.getCategoryId() <= 0) return -1;
        if (policy.getPolicyText() == null || policy.getPolicyText().trim().isEmpty()) return -1;

        if (policyDao.existsByCategoryId(policy.getCategoryId())) {
            return -2;
        }

        return policyDao.insert(policy);
    }

    public boolean updatePolicy(Policy policy) {
        if (policy == null || policy.getId() <= 0) return false;
        if (policy.getCategoryId() <= 0) return false;
        if (policy.getPolicyText() == null || policy.getPolicyText().trim().isEmpty()) return false;

        if (!policyDao.exists(policy.getId())) {
            return false;
        }

        Policy existing = policyDao.getById(policy.getId());
        if (existing.getCategoryId() != policy.getCategoryId()) {
            Policy conflictPolicy = policyDao.getByCategoryId(policy.getCategoryId());
            if (conflictPolicy != null && conflictPolicy.getId() != policy.getId()) {
                return false;
            }
        }

        return policyDao.update(policy);
    }

    public boolean deletePolicy(int id) {
        if (id <= 0) return false;
        if (!policyDao.exists(id)) return false;
        return policyDao.delete(id);
    }

    public boolean deletePolicyByCategoryId(int categoryId) {
        if (categoryId <= 0) return false;
        return policyDao.deleteByCategoryId(categoryId);
    }
}
