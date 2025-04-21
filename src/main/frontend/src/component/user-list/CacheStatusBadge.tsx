import React from 'react';
import styled from 'styled-components';

const BadgeWrapper = styled.div<{ visible: boolean; type: 'HIT' | 'MISS' }>`
  position: fixed;
  right: 20px;
  bottom: 20px;
  padding: 14px 24px;
  background-color: ${(props) => (props.type === 'HIT' ? '#28a745' : 'firebrick')};
  color: #fff;
  border-radius: 12px;
  font-size: 18px;
  font-weight: bold;
  z-index: 9999;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.25);
  opacity: ${(props) => (props.visible ? 1 : 0)};
  transform: ${(props) => (props.visible ? 'translateY(0)' : 'translateY(10px)')};
  transition: all 0.4s ease;
`;

interface CacheStatusBadgeProps {
  visible: boolean;
  type: 'HIT' | 'MISS';
  message?: string;
  speed?: string;
  ttlSeconds?: number | null;
}

const CacheStatusBadge = ({ visible, type, message, speed, ttlSeconds }: CacheStatusBadgeProps) => {
  return (
    <BadgeWrapper visible={visible} type={type}>
      {message}
      {speed && `- 조회 속도: ${speed}`}
      {type === 'HIT' && ttlSeconds !== null && `, TTL: ${ttlSeconds}초`}
    </BadgeWrapper>
  );
};

export default CacheStatusBadge;