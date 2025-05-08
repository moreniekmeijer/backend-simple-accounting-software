//package nl.moreniekmeijer.backendsimpleaccountingsoftware.controllers;
//
//import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvestmentInputDto;
//import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvestmentOutputDto;
//import nl.moreniekmeijer.backendsimpleaccountingsoftware.services.InvestmentService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/investments")
//public class InvestmentController {
//
//    private final InvestmentService investmentService;
//
//    public InvestmentController(InvestmentService investmentService) {
//        this.investmentService = investmentService;
//    }
//
//    @PostMapping
//    public ResponseEntity<InvestmentOutputDto> completeInvestment(@RequestParam Long expenseId,
//                                                                  @RequestBody InvestmentInputDto dto) {
//        InvestmentOutputDto created = investmentService.completeInvestment(expenseId, dto);
//        return ResponseEntity.ok(created);
//    }
//}
