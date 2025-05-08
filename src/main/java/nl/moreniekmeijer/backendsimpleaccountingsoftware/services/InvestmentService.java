//package nl.moreniekmeijer.backendsimpleaccountingsoftware.services;
//
//import jakarta.transaction.Transactional;
//import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvestmentInputDto;
//import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvestmentOutputDto;
//import nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers.InvestmentMapper;
//import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Expense;
//import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.InvestmentDetails;
//import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.ExpenseRepository;
//import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.InvestmentRepository;
//import org.springframework.stereotype.Service;
//
//@Service
//public class InvestmentService {
//
//    private final ExpenseRepository expenseRepository;
//    private final InvestmentRepository investmentRepository;
//
//    public InvestmentService(ExpenseRepository expenseRepository, InvestmentRepository investmentRepository) {
//        this.expenseRepository = expenseRepository;
//        this.investmentRepository = investmentRepository;
//    }
//
//    @Transactional
//    public InvestmentOutputDto completeInvestment(Long expenseId, InvestmentInputDto dto) {
//        // 1. Zoek de bijbehorende expense
//        Expense expense = expenseRepository.findById(expenseId)
//                .orElseThrow(() -> new IllegalArgumentException("Expense met ID " + expenseId + " niet gevonden"));
//
//        // 2. Check of deze expense als investering is gemarkeerd
//        if (!"investering".equalsIgnoreCase(expense.getCategory())) {
//            throw new IllegalStateException("Deze expense is niet als investering gemarkeerd");
//        }
//
//        // 3. Check of er al een InvestmentDetails bestaat
//        if (expense.getInvestmentDetails() != null) {
//            throw new IllegalStateException("Er is al een investering gekoppeld aan deze expense");
//        }
//
//        // 4. Maak InvestmentDetails aan en koppel deze aan de expense
//        InvestmentDetails details = InvestmentMapper.toEntity(expense, dto);
//        InvestmentDetails saved = investmentRepository.save(details);
//
//        // 5. Optioneel: koppel terug in Expense (indien je bi-directionele relatie hebt)
//        expense.setInvestmentDetails(saved);
//        expenseRepository.save(expense); // niet strikt nodig voor eenzijdige relatie, maar netjes
//
//        return InvestmentMapper.toDto(saved);
//    }
//}
