import React from 'react';
import styled from "styled-components";

const PagingTitle = styled.h2`
  font-size: 25px;
  font-weight: 800;
  color: #333;
  margin-right: 12px;
  background: linear-gradient(90deg, #cd5c5c, #cd5c5c);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
`;

const PagingTypeWrapper = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 35px;
  margin-bottom: 15px;
  position: sticky; /* 상단에 고정 */
  top: 0px; /* 고정될 위치 */
  background-color: #fff; /* 배경색을 흰색으로 설정하여 콘텐츠가 겹치지 않게 함 */
  z-index: 100; /* 다른 요소들 위에 고정되도록 설정 */
  padding-top: 20px; /* 상단 간격 조정 */
  padding-bottom: 10px; /* 하단 간격 조정 */
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 30px;
`;

const PagingTypeButton = styled.button<{ active: boolean }>`
  padding: 7px 15px;
  border: 2px solid indianred;
  background-color: ${(props) => (props.active ? 'indianred' : '#fff')};
  color: ${(props) => (props.active ? '#fff' : 'indianred')};
  border-radius: 8px;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;

  &:hover {
    background-color: ${(props) => (props.active ? '#b64949' : '#fbeaea')};
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  }
`;

interface PagingTypeSelectorProps {
  currentType: 'cache-cursor' | 'cursor' | 'offset';
  onChange: (type: 'cache-cursor' | 'cursor' | 'offset') => void;
}

const PagingTypeSelector = ({ currentType, onChange }: PagingTypeSelectorProps) => {
  return (
    <PagingTypeWrapper>
      <PagingTitle>조회 페이징 타입</PagingTitle>
      <ButtonGroup>
        <PagingTypeButton
          active={currentType === 'cache-cursor'}
          onClick={() => onChange('cache-cursor')}
        >
          캐시 + 커서
        </PagingTypeButton>
        <PagingTypeButton
          active={currentType === 'cursor'}
          onClick={() => onChange('cursor')}
        >
          커서
        </PagingTypeButton>
        <PagingTypeButton
          active={currentType === 'offset'}
          onClick={() => onChange('offset')}
        >
          오프셋
        </PagingTypeButton>
      </ButtonGroup>
    </PagingTypeWrapper>
  );
};

export default PagingTypeSelector;