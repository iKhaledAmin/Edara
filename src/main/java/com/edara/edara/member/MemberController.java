package com.edara.edara.member;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;


    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody @Valid MemberRequest memberRequest) {
        return new ResponseEntity<>(memberService.add(memberRequest), HttpStatus.CREATED);
    }
    @PutMapping("/update/{memberId}")
    public ResponseEntity<?> update(@PathVariable Long memberId,@RequestBody @Valid MemberRequest projectRequest) {
        return new ResponseEntity<>(memberService.update(memberId,projectRequest), HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/delete/{userCode}/{projectId}")
    public ResponseEntity<?> delete(@PathVariable String userCode, @PathVariable Long projectId) {
        memberService.delete(userCode, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/get/{memberId}")
    ResponseEntity<?> getById( @PathVariable Long memberId){
        return new ResponseEntity<>(memberService.getResponseById(memberId), HttpStatus.OK);
    }
    @GetMapping("/get-all-of-project/{projectId}")
    ResponseEntity<?> getALlOfProject( @PathVariable Long projectId){
        return new ResponseEntity<>(memberService.getAllResponseByProjectId(projectId), HttpStatus.OK);
    }



}
